# Schemes and Clipboards

A `Scheme` (`structure/src/main/java/me/wyne/wutils/structure/scheme/Scheme.java:20`)
supplies the WorldEdit `Clipboard` a `Structure` pastes. There are two
implementations — a single fixed schematic, or one drawn at random from a
set — and a `Factory` that picks between them from config.

## Choosing between `FileScheme` and `RandomScheme`

`Scheme.Factory`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/Scheme.java:61-70`)
dispatches purely on whether the `scheme` key is present in the section: if
it is, `FileScheme.Factory` builds a fixed scheme; otherwise
`RandomScheme.Factory` builds a random one (which itself reads a different
key, `schemes`).

<!-- allow-code-fences -->
```yaml
# fixed: always pastes this one file
my-structure:
  scheme:
    scheme: schematics/tower.schem   # relative to the plugin's data folder

# random: picks uniformly among every file matching a regex
my-structure:
  scheme:
    schemes: schematics/tower-.*\.schem   # directory + filename regex
```

## `FileScheme`

`FileScheme` (`structure/src/main/java/me/wyne/wutils/structure/scheme/FileScheme.java:23`)
wraps a single schematic file. The path is resolved relative to the owning
plugin's data folder (`PluginUtils.getPlugin().getDataFolder()`,
`structure/src/main/java/me/wyne/wutils/structure/scheme/FileScheme.java:31-34`),
or an explicit `Plugin`/`File` if you construct it programmatically. The
file's format is auto-detected from its extension via WorldEdit's
`ClipboardFormats.findByFile`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/FileScheme.java:56-64`) —
`.schem` (Sponge Schematic) is what the `Factory`'s default filename implies,
but any format WorldEdit itself recognizes works.

The constructor eagerly preloads the clipboard so a missing or malformed
file is logged loudly at startup — but a preload failure is **swallowed**,
not thrown from the constructor
(`structure/src/main/java/me/wyne/wutils/structure/scheme/FileScheme.java:66-76`).
`getClipboard()`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/FileScheme.java:78-82`)
retries the load lazily if the eager attempt never produced a clipboard, so
a file that didn't exist at startup but appears later still works on first
use — but a file that's still missing or still malformed throws from
`getClipboard()` itself (`IllegalArgumentException` for an undetectable
format, `RuntimeException` for a read failure).

## `RandomScheme`

`RandomScheme` (`structure/src/main/java/me/wyne/wutils/structure/scheme/RandomScheme.java:26`)
picks one `FileScheme` uniformly at random on every `getClipboard()` call
(`structure/src/main/java/me/wyne/wutils/structure/scheme/RandomScheme.java:66-70`) —
so a `Structure` reusing the same `RandomScheme` across multiple placements
can paste a different schematic each time.

**The `schemes` value is not a directory — it's a directory plus a regex.**
`listSchemes`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/RandomScheme.java:47-61`)
splits the string on the last path separator: everything before it is the
directory to list, and everything after it is compiled as a `Pattern` and
matched (via `Matcher#matches`, so it must match the *whole* filename) against
every file directly in that directory. This is the single most surprising
thing in this package — `schemes: schematics/house` looks like a directory
path but is actually "list `schematics/` for files whose name equals
literally `house`". A real config almost always wants a pattern like the
one above (`tower-.*\.schem`), not a bare name.

A directory with zero matches yields an empty scheme list, not an error at
construction time. `getClipboard()`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/RandomScheme.java:66-70`)
throws `IllegalStateException` only when actually called against an empty
list — so a misconfigured regex fails at generation time, not at load time.

`RandomScheme.ofPaths` / `ofFiles`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/RandomScheme.java:72-78`)
build one from an explicit collection instead of a directory scan, for
programmatic use.

## `Scheme.toWorld`: bounding box, not corner mapping

`Scheme.toWorld`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/Scheme.java:32-55`)
maps a clipboard's region into world coordinates under a `Transform`. It
does this by mapping **all eight corners** of the clipboard's bounding box
and taking the min/max of the mapped results, rather than transforming just
the region's own min and max corners. That matters because a rotation can
swap which corner ends up "minimum" on a given axis — transforming only the
original min/max would produce a wrong (and possibly degenerate) box for
any structure placed with a 90°/270° rotation. This is what both
`MarginRegion` and `SchemeRegion` call to size the protected region (see
[Regions and Flags](regions.md)).

## `ClipboardScan` and `ClipboardScanCache`

`ClipboardScan` (`structure/src/main/java/me/wyne/wutils/structure/scheme/ClipboardScan.java:19`)
finds marker blocks inside a schematic — the kind of thing an
`EditSessionModifier` or a custom integration uses to locate, say, every
chest position in a template before pasting. `find(Mask)` and
`find(BlockType...)`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/ClipboardScan.java:33-44`)
walk the whole clipboard region and return matching clipboard-local
positions. `ClipboardScan.toWorld`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/ClipboardScan.java:50-53`)
converts one such position to world coordinates given the clipboard's
origin, the placement point, and the paste transform — the single-point
version of what `Scheme.toWorld` does for a whole region.

Each `ClipboardScan` call re-scans the clipboard from scratch.
`ClipboardScanCache`
(`structure/src/main/java/me/wyne/wutils/structure/scheme/ClipboardScanCache.java:31`)
memoizes results so repeated lookups against the same clipboard (e.g. the
same schematic pasted many times) don't re-scan it every time:

- Cached per `Clipboard` in a `WeakHashMap` keyed by identity
  (`structure/src/main/java/me/wyne/wutils/structure/scheme/ClipboardScanCache.java:33-34`),
  so entries are collected once nothing else references the clipboard.
- Within one clipboard's entry, results are further keyed by an `Object`: a
  `Set<BlockType>` for the block-type overload, or a caller-chosen `String`
  for the two overloads that take one
  (`structure/src/main/java/me/wyne/wutils/structure/scheme/ClipboardScanCache.java:40-56`).
- **All three `find` overloads share one key namespace per clipboard.** A
  caller-chosen `String` key must be unique for what it scans — reusing a
  key for a different `Mask` or scanner returns the first scan's stale
  result instead of re-running.
- Every `find` overload wraps its result in `List.copyOf(...)`
  (`structure/src/main/java/me/wyne/wutils/structure/scheme/ClipboardScanCache.java:40-56`),
  so returned lists are immutable copies, safe to hand out without the
  caller defensively copying.
- `invalidate(Clipboard)` and `clear()`
  (`structure/src/main/java/me/wyne/wutils/structure/scheme/ClipboardScanCache.java:64-70`)
  drop cached entries manually, for when a clipboard's contents change
  in place.

## See also

- [WUtils Structure](structure.md) — the overall pipeline; `Scheme` supplies the clipboard in Phase 1.
- [Regions and Flags](regions.md) — `MarginRegion`/`SchemeRegion` use `Scheme.toWorld` to size the protected region.
