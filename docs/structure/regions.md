# Regions and Flags

A `StructureRegion` (`structure/src/main/java/me/wyne/wutils/structure/region/StructureRegion.java:20`)
builds the WorldGuard `ProtectedCuboidRegion` a structure is protected by.
`getRegion(Clipboard, Location, Transform)`
(`structure/src/main/java/me/wyne/wutils/structure/region/StructureRegion.java:36`)
constructs it around the pasted clipboard's world-space bounds; the two
implementations differ only in how those bounds are adjusted. Requires
WorldEdit and WorldGuard — see [Dependencies](structure.md#dependencies).

## Choosing between `SchemeRegion` and `MarginRegion`

`StructureRegion.Factory`
(`structure/src/main/java/me/wyne/wutils/structure/region/StructureRegion.java:53-62`)
dispatches on a single key: if `margin` is present, `MarginRegion`;
otherwise `SchemeRegion`.

<!-- allow-code-fences -->
```yaml
# SchemeRegion: protected region sized exactly to the pasted clipboard
my-structure:
  region:
    id: my-structure-<x>x<y>y<z>z
    priority: 10

# MarginRegion: protected region expanded by `margin` blocks on every axis
my-structure:
  region:
    id: my-structure-<x>x<y>y<z>z
    margin: 5   # extra blocks of buffer around the pasted footprint
```

`SchemeRegion` (`structure/src/main/java/me/wyne/wutils/structure/region/SchemeRegion.java:17`)
sizes the region to exactly `Scheme.toWorld(clipboard, location, transform)`'s
bounds. `MarginRegion`
(`structure/src/main/java/me/wyne/wutils/structure/region/MarginRegion.java:20`)
does the same, then subtracts/adds `margin` blocks from the minimum/maximum
corner
(`structure/src/main/java/me/wyne/wutils/structure/region/MarginRegion.java:33-38`) —
useful for giving a structure a buffer zone (no build damage right up to
its walls) without changing what gets pasted. Both call `Scheme.toWorld`
(see [Schemes and Clipboards](schemes.md#schemetoworld-bounding-box-not-corner-mapping))
to get correct bounds under rotation.

## `RegionData`

`RegionData` (`structure/src/main/java/me/wyne/wutils/structure/region/RegionData.java:25`)
carries the metadata every `StructureRegion` applies to the built
`ProtectedCuboidRegion`: `id`, `transient`, `priority`, and `flags`.

- **`id`** — required. A missing `id` throws `IllegalArgumentException`
  naming the offending config path
  (`structure/src/main/java/me/wyne/wutils/structure/region/RegionData.java:64-66`).
  `StructureRegion.validateId`
  (`structure/src/main/java/me/wyne/wutils/structure/region/StructureRegion.java:42-47`)
  first substitutes `<x>`, `<y>`, `<z>` with the placement's block
  coordinates, then strips every character outside
  `[A-Za-z0-9_,'+/-]` — WorldGuard rejects most punctuation in a region id,
  so an id like `my-structure-<x>x<y>y<z>z` becomes something like
  `my-structure-104x67y-88z`.
- **`transient`** — default `false`. A transient region is not written to
  WorldGuard's own persistent storage.
- **`priority`** — default `0`. Standard WorldGuard region priority,
  higher wins on overlap.
- **`flags`** — resolved against WorldGuard's **live** flag registry via
  `Flags.fuzzyMatchFlag`
  (`structure/src/main/java/me/wyne/wutils/structure/region/RegionData.java:80-90`),
  so third-party plugin flags (from another WorldGuard-integrated plugin)
  work exactly like built-in ones. An unknown flag name throws
  `IllegalArgumentException` naming the key
  (`structure/src/main/java/me/wyne/wutils/structure/region/RegionData.java:82-83`);
  a value that flag's own parser rejects throws naming the key and the
  parser's message
  (`structure/src/main/java/me/wyne/wutils/structure/region/RegionData.java:85-89`).

<!-- allow-code-fences -->
```yaml
my-structure:
  region:
    id: my-structure-<x>x<y>y<z>z
    transient: false   # default; set true to skip WorldGuard's own persistence
    priority: 10
    flags:
      pvp: deny
      mob-spawning: deny
      build: deny
      greeting: "&aYou entered My Structure!"
```

## The condition vocabulary

`RegionCondition.FACTORY_MAP`
(`structure/src/main/java/me/wyne/wutils/structure/region/condition/RegionCondition.java:23-30`)
lives in the same shared `conditions` section as location conditions (see
[Locations and Conditions](locations.md#the-condition-vocabulary)) — the
two vocabularies are interleaved freely because `Structure.fromConfig`
reads that one section through both `LocationCondition.FACTORY_MAP` and
`RegionCondition.FACTORY_MAP`
(`structure/src/main/java/me/wyne/wutils/structure/Structure.java:213-214`).

| Config key | Value type | Implementation |
|---|---|---|
| `region-whitelist` | list of WorldGuard region ids | `RegionWhitelistCondition` |
| `altitude-difference` | comparator string, e.g. `"<=8"` | `AltitudeDifferenceCondition` |

<!-- allow-code-fences -->
```yaml
my-structure:
  conditions:
    region-whitelist: [wilderness, spawn-buffer]   # only these regions may overlap
    altitude-difference: "<=8"                       # reject uneven terrain
```

`RegionWhitelistCondition`
(`structure/src/main/java/me/wyne/wutils/structure/region/condition/RegionWhitelistCondition.java:17`)
fetches every WorldGuard region overlapping the candidate protected region
and requires all of them to be in the configured list
(`structure/src/main/java/me/wyne/wutils/structure/region/condition/RegionWhitelistCondition.java:29-37`) —
an empty or absent list of overlapping regions passes trivially, but any
overlap with a region not on the whitelist fails the candidate.

`AltitudeDifferenceCondition`
(`structure/src/main/java/me/wyne/wutils/structure/region/condition/AltitudeDifferenceCondition.java:19`)
samples ocean-floor terrain height (`HeightMap.OCEAN_FLOOR`) at nine
points across the structure's footprint — the origin, the four footprint
corners, and the four corner-to-corner midpoints
(`structure/src/main/java/me/wyne/wutils/structure/region/condition/AltitudeDifferenceCondition.java:33-71`) —
and compares the difference between the highest and lowest sample against
the configured comparator. Use it to reject placements over cliffs or
ravines that a flat schematic would paste awkwardly onto.

## Timing: conditions run after modifiers

**Region conditions are evaluated after region modifiers have already run**,
against the final protected region shape — this is the opposite order from
location conditions, which run before any placement modifier touches the
location. `Structure#create`'s Phase 2
(`structure/src/main/java/me/wyne/wutils/structure/Structure.java:244-266`)
applies every `RegionModifier` (expand/contract/outset/inset — see
[Modifiers](modifiers.md)) to the region *first*, then checks
`RegionCondition`s against the modified result. Practically: an
`altitude-difference` or `region-whitelist` condition sees the region as it
will actually be registered with WorldGuard, margin/expansion included —
not the raw clipboard-sized footprint.

## See also

- [WUtils Structure](structure.md) — the full pipeline; Phase 2 is where region modifiers and region conditions run.
- [Schemes and Clipboards](schemes.md) — `Scheme.toWorld`, used by both `StructureRegion` implementations.
- [Locations and Conditions](locations.md) — the sibling condition vocabulary, sharing the same `conditions` config section.
- [Modifiers](modifiers.md) — `RegionModifier` (expand/contract/outset/inset).
