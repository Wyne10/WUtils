# Modifiers

Modifiers are the config-driven hooks a [Structure](structure.md) recipe runs at fixed
points in its placement pipeline: they pick a paste transform, nudge the placement point,
reshape the WorldGuard region, and configure the snapshot/paste/edit-session operations
`WorldStructure` performs. Every modifier is written under a structure's `modifiers:`
YAML section, keyed by a `StructureModifier` config key.

This page covers `me.wyne.wutils.structure.modifier` — the six modifier interfaces, the
`StructureModifier` key enum, and the `clipboard/`, `location/`, `region/`, `paste/`, and
`snapshot/` subpackages. It also folds in `me.wyne.wutils.structure.mask` and
`me.wyne.wutils.structure.pattern` (two classes each, not worth their own pages). The much
larger `modifier/edit/` package — 30 classes implementing `EditSessionModifier` — has its
own page: [Terrain Edit Modifiers](edit-modifiers.md).

## Registration order is application order

**YAML key order does not decide when a modifier runs.** Application order is fixed by
`Structure.STRUCTURE_MODIFIER_MAP`
(`structure/src/main/java/me/wyne/wutils/structure/Structure.java:92-128`), a static
registry that maps every built-in `StructureModifier` key to its factory, in a fixed
order. Whatever order you write `rotate`, `outset`, `smooth`, etc. under `modifiers:` in
your YAML, they are looked up and applied in `STRUCTURE_MODIFIER_MAP`'s order, not the
file's.

The same applies to the programmatic `Structure.Builder`: `build()`
(`structure/src/main/java/me/wyne/wutils/structure/Structure.java:434-458`) re-sorts
whatever was added via `.modifier(...)` into `STRUCTURE_MODIFIER_MAP`'s order, appending
any unrecognised (custom) modifier key afterward in the order it was added.

## The six interfaces, by pipeline stage

Each modifier kind is a single-method functional interface. `Structure.create` and
`WorldStructure` (see [the pipeline](structure.md#the-pipeline) and
[spawning](structure.md#spawning) for the full picture) invoke them in this order:

| # | Interface | Runs | Mutates |
|---|---|---|---|
| 1 | `ClipboardModifier` (`structure/src/main/java/me/wyne/wutils/structure/modifier/ClipboardModifier.java:14`) | First, on the `ClipboardHolder` wrapping the scheme's clipboard, before any location or region is computed | Combines onto `ClipboardHolder#getTransform()` — this is where rotation/flip is chosen |
| 2 | `LocationModifier` (`structure/src/main/java/me/wyne/wutils/structure/modifier/LocationModifier.java:13`) | After the highest-block lookup | Returns an adjusted placement `Location`; does not mutate the input |
| 3 | `RegionModifier` (`structure/src/main/java/me/wyne/wutils/structure/modifier/RegionModifier.java:18`) | Before region conditions are evaluated | Returns a replacement `ProtectedCuboidRegion` — WorldGuard regions are immutable in bounds, so implementations build a new region and copy the old one's id/flags/priority onto it |
| 4 | `SnapshotModifier` (`structure/src/main/java/me/wyne/wutils/structure/modifier/SnapshotModifier.java:14`) | During `WorldStructure#spawn()`, before the rollback snapshot is captured | Configures the `ForwardExtentCopy` that captures pre-paste world state |
| 5 | `PasteModifier` (`structure/src/main/java/me/wyne/wutils/structure/modifier/PasteModifier.java:13`) | During `spawn()`, after the snapshot | Configures the `PasteBuilder` used to paste the clipboard |
| 6 | `EditSessionModifier` (`structure/src/main/java/me/wyne/wutils/structure/modifier/EditSessionModifier.java:14`) | During `spawn()`, after the paste, on a fresh `EditSession` | Edits terrain in and around the pasted region — this is the entire `modifier/edit/` package |

### The exception asymmetry

Where a modifier runs determines what happens when it throws, and the two halves behave
oppositely:

- **`ClipboardModifier`, `LocationModifier`, `RegionModifier`** are invoked directly inside
  `Structure.create` (`structure/src/main/java/me/wyne/wutils/structure/Structure.java:283-289`,
  `:247-250`). An exception here is **not caught** — it propagates to the caller and aborts
  the whole placement attempt.
- **`SnapshotModifier`, `PasteModifier`, `EditSessionModifier`** are applied by
  `WorldStructure` during `spawn()`
  (`structure/src/main/java/me/wyne/wutils/structure/WorldStructure.java:193-199`, `:217-223`,
  `:228-234`). An exception here is **logged and skipped** — one misbehaving modifier does
  not sink the paste or the other modifiers.

In short: modifiers that run before a location/region is finalized are load-bearing —
get them wrong and generation fails outright. Modifiers that run during `spawn()` are
best-effort — a bad mask string or an over-broad radius degrades gracefully instead of
losing the whole structure.

## `StructureModifier` config keys

`StructureModifier` (`structure/src/main/java/me/wyne/wutils/structure/modifier/StructureModifier.java:11`)
is the canonical enum of config keys; its declaration order is also
`STRUCTURE_MODIFIER_MAP`'s registration order. Most keys are guessable from the constant
name — `EDIT_EXTINGUISH`'s key is the terse `ex`.

| Config key | Modifier | Grammar | Notes |
|---|---|---|---|
| `rotate` | `RotateClipboardModifier` | space-separated list of angles, or `true`/`random`/`any`/empty | See [rotate and flip](#rotate-and-flip) below |
| `flip` | `FlipClipboardModifier` | space-separated list of axes, or `true`/`random`/`any`/empty | See [rotate and flip](#rotate-and-flip) below |
| `altitude` | `AltitudeLocationModifier` | an [`IntOperation`](../common/operations.md) string, e.g. `+2`, `-1`, `5` | Applied to the placement Y after the highest-block lookup; default `+0` |
| `expand` | `ExpandRegionModifier` | `<direction> <amount>` | See [expand/contract vs. outset/inset](#expandcontract-vs-outsetinset) |
| `contract` | `ContractRegionModifier` | `<direction> <amount>` | ditto |
| `outset` | `OutsetRegionModifier` | `<amount> [-h] [-v]` | ditto |
| `inset` | `InsetRegionModifier` | `<amount> [-h] [-v]` | ditto |
| `snapshotEntities` | `EntitiesSnapshotModifier` | boolean, default `false` | Whether the rollback snapshot copies entities |
| `snapshotRemoveEntities` | `RemoveEntitiesSnapshotModifier` | boolean, default `false` | Whether taking the snapshot removes entities from the source |
| `snapshotBiomes` | `BiomesSnapshotModifier` | boolean, default `false` | Whether the rollback snapshot copies biomes |
| `snapshotSourceMask` | `SourceMaskSnapshotModifier` | a WorldEdit mask string | Restricts the snapshot copy to matching source blocks |
| `pasteEntities` | `EntitiesPasteModifier` | boolean, default `false` | Whether the paste copies entities from the clipboard |
| `pasteBiomes` | `BiomesPasteModifier` | boolean, default `false` | Whether the paste copies biomes from the clipboard |
| `pasteIgnoreAir` | `IgnoreAirPasteModifier` | boolean, default `false` | Whether clipboard air blocks are skipped rather than pasted |
| `pasteSourceMask` | `MaskSourcePasteModifier` | a WorldEdit mask string | Restricts the paste to clipboard blocks matching the mask |
| `replace`, `set`, `grow`, `smooth`, `naturalize`, `flora`, `forest`, `biome`, `deform`, `snow`, `snowIfCold`, `adaptSurface`, `thaw`, `green`, `ex`, `butcher`, `deltree`, `dropFloating` | `EditSessionModifier` implementations under `modifier/edit/` | — | Full grammar and behavior in [Terrain Edit Modifiers](edit-modifiers.md) |

### Rotate and flip

`RotateClipboardModifier` and `FlipClipboardModifier`
(`structure/src/main/java/me/wyne/wutils/structure/modifier/clipboard/RotateClipboardModifier.java:24`,
`FlipClipboardModifier.java:25`) both parse their config value into a **list of choices**,
one of which is picked at random (`ThreadLocalRandom`) independently for every placement —
not a single fixed value.

`RotateSettings.parse` (`structure/src/main/java/me/wyne/wutils/structure/modifier/clipboard/RotateSettings.java:26-47`)
splits the value on whitespace; each token must be an integer multiple of 90 (normalised
into `[0,360)`) or parsing throws. An empty string, `true`, `random`, or `any`
(case-insensitive) yields the built-in default set `0 90 180 270`.

`FlipSettings.parse` (`structure/src/main/java/me/wyne/wutils/structure/modifier/clipboard/FlipSettings.java:31-44`)
works the same way over axis tokens (`x`/`west`/`east`, `y`/`up`/`down`, `z`/`north`/`south`,
`none`/`0`/`-`); the same empty/`true`/`random`/`any` shortcut yields the default set
`none x z`.

### `expand`/`contract` vs. `outset`/`inset`

Both pairs resize the placement region (`region/DeltaRegionModifier.java:12-43`), but along
different axes:

- **`expand`/`contract`** (`DirectionalAmount.parse`,
  `structure/src/main/java/me/wyne/wutils/structure/modifier/region/DirectionalAmount.java:20-25`)
  take `<direction> <amount>` — a WorldEdit `Direction` constant name (case-insensitive,
  e.g. `north`, `up`) and an integer amount, defaulting to `1`. `expand` grows the region
  that far in that direction; `contract` shrinks it.
- **`outset`/`inset`** (`ScopedAmount.parse`,
  `structure/src/main/java/me/wyne/wutils/structure/modifier/region/ScopedAmount.java:16-29`)
  take `<amount>` plus optional `-h`/`-v` scope flags. Specifying **neither** flag, or
  **both**, scopes the change to both axes; specifying exactly one restricts it to that
  axis. `outset` grows the region on all edges by `amount`; `inset` shrinks it.

## Mask and pattern parsing

`MaskUtils` and `PatternUtils`
(`structure/src/main/java/me/wyne/wutils/structure/mask/MaskUtils.java:24`,
`structure/src/main/java/me/wyne/wutils/structure/pattern/PatternUtils.java:24`) wrap
WorldEdit's own mask/pattern parsers with `setRestricted(false)` and rethrow any parse
failure as an `IllegalArgumentException` naming the offending input, instead of WorldEdit's
own `InputParseException`. Both require WorldEdit on the classpath, like the rest of this
module.

The useful part is *why* the lazy variants — `LazyMask`, `LazyPattern`, and the modifier
package's own `LazyMaskPatternPair`
(`structure/src/main/java/me/wyne/wutils/structure/modifier/LazyMaskPatternPair.java:19`) —
exist at all: they keep the mask/pattern string unparsed and re-parse it on every
`getMask`/`getPattern`/`getEager` call, so the same config value can be resolved later
against whichever world or `Extent` is in play at the point of use, rather than being
locked to whichever context happened to be available when the config was read. Use the
eager counterpart, `MaskPatternPair`
(`structure/src/main/java/me/wyne/wutils/structure/modifier/MaskPatternPair.java:17`), when
the context is already known up front and re-parsing on every use is wasted work.

Which overload to call matters for both parsers: the `ParserContext` passed to WorldEdit's
factory determines which mask/pattern syntaxes resolve. No context unlocks only
context-free syntaxes; passing a `World` or `Extent` unlocks syntaxes that need one (e.g.
relative-position masks). The two-argument `World`-based overloads differ only in whether
the `World` is Bukkit's or WorldEdit's type — pick whichever you already have. In the
world-and-extent overload, `setWorld` is called before `setExtent` internally because it
would otherwise overwrite the extent.

## A working example

<!-- allow-code-fences -->
```yaml
my-structure:
  modifiers:
    rotate: any            # pick a random 90-degree rotation each placement (0/90/180/270)
    flip: none x            # pick a random flip: none or mirrored along X (never Z here)
    altitude: +1             # place 1 block above the highest-block lookup, e.g. to float a dock
    outset: 6 -h              # grow the WorldGuard region 6 blocks horizontally only (not vertically)
    snapshotEntities: true      # rollback snapshot also restores any entities removed/moved by later steps
    pasteEntities: true           # paste copies entities baked into the schematic (e.g. villagers)
    pasteSourceMask: "!air"         # skip clipboard air blocks when pasting, same effect as pasteIgnoreAir
```

## See also

- [WUtils Structure](structure.md) — the full placement pipeline these modifiers plug into.
- [Terrain Edit Modifiers](edit-modifiers.md) — the `modifier/edit/` package (`EditSessionModifier`).
- [Regions and Flags](regions.md) — what a `RegionModifier` is reshaping.
- [Comparators and Operations](../common/operations.md) — `IntOperation`, used by `altitude`.
- [Attributes and Containers](../configurables/attributes.md) — `AttributeMap`/`AttributeConfigurable`, the machinery that resolves YAML keys to modifier factories and preserves registration order.
