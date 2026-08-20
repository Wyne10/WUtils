# Terrain Edit Modifiers

`me.wyne.wutils.structure.modifier.edit` is where most of a structure's terrain shaping
lives: 18 concrete `EditSessionModifier` implementations, three abstract base classes, and
nine `*Settings` records that parse their YAML value. See [Modifiers](modifiers.md) for the
`EditSessionModifier` contract itself — it runs after the paste, on a fresh `EditSession`,
and an implementation that throws is logged and skipped by `WorldStructure` rather than
aborting the spawn.

## The two shapes

Every concrete modifier here is built on one of two abstract bases, which differ in where
they operate relative to the pasted structure.

**`RadiusEditModifier`**
(`structure/src/main/java/me/wyne/wutils/structure/modifier/edit/RadiusEditModifier.java:21`)
operates on a **sphere** centred on the pasted region's `getCenter()`. The attribute's value
*is* the radius. `apply` resolves the centre and delegates to the abstract `applyAt`, which
subclasses implement with the actual WorldEdit call; a `MaxChangedBlocksException` from that
call is wrapped in a `RuntimeException` naming the modifier's config key, so a log line
identifies which entry overran the block-change limit. Two static helpers, `columnBase` and
`columnTop`, give subclasses the full-height column over the region for operations (snow,
thaw, foliage) that need to scan vertically rather than touch a single point.

**`MarginEditModifier<V>`**
(`structure/src/main/java/me/wyne/wutils/structure/modifier/edit/MarginEditModifier.java:41`)
operates on a **ring of terrain around** the pasted structure. Its mechanics are the least
obvious thing in this package:

1. `apply` clones the pasted region and expands it by `margin()` blocks on every axis.
2. If `excludeFootprint()` is `true` (the default), it installs a mask on the `EditSession`
   that excludes the structure's own footprint, via `outsideFootprint`
   (`MarginEditModifier.java:72-84`), so the edit reshapes the surroundings without carving
   into the pasted building.
3. `applyEdit` is then called with the expanded region as the area to edit, and the
   original unexpanded region passed through unchanged so subclasses can still locate the
   footprint within it.
4. The mask is restored in a `finally` — any mask already on the session before `apply` ran
   is **intersected** with the exclusion mask, not replaced by it, and that combined state
   is what gets undone afterward.

The exclusion test is 2D — X/Z only (`MarginEditModifier.java:72-84`) — so it excludes the
**full-height column** above and below the structure's footprint, not a box bounded by the
structure's own Y range.

## The surprising part: several edit modifiers also grow the region

An `EditSessionModifier` edits terrain, but several of these classes *also implement
`RegionModifier`* and grow the WorldGuard protected region to cover the terrain they are
about to touch:

- **`MarginEditModifier`** itself implements `RegionModifier`
  (`MarginEditModifier.java:112-125`): its `apply(ProtectedCuboidRegion, Region)` expands
  the protected region's bounds by `margin()` on every axis, same as it expands the edit
  area. Every concrete subclass inherits this, so adding `smooth: 10 2` to a structure
  silently enlarges its protected region by 10 blocks in every direction.
- **`RegionRadiusEditModifier`**
  (`structure/src/main/java/me/wyne/wutils/structure/modifier/edit/RegionRadiusEditModifier.java:13`)
  is a `RadiusEditModifier` that also implements `RegionModifier`, growing the protected
  region to cover the sphere it is about to edit — the radius equivalent of what
  `MarginEditModifier` does for its margin.
- **Plain `RadiusEditModifier`** subclasses (`ButcherEditModifier`, `ExtinguishEditModifier`)
  do **not** grow the region — they only remove entities or fire, which WorldGuard flags
  don't gate the same way block edits do.
- **`AdaptSurfaceEditModifier`** is the one exception in the margin family: it has a margin
  concept but only *reads* the surrounding terrain to pick a replacement block, never writes
  to it, so it does not implement `RegionModifier` and does not grow the region.
- **`ReplaceEditModifier`** and **`SetEditModifier`** operate directly on the pasted region
  with no expansion at all, so region growth does not apply to them either.

## All 18 concrete modifiers

| Config key | Class | Shape | Grows region? | What it does |
|---|---|---|---|---|
| `replace` | `ReplaceEditModifier` | pasted region, no expansion | no | Replaces blocks matching a mask with a pattern via `EditSession#replaceBlocks` |
| `set` | `SetEditModifier` | pasted region, no expansion | no | Sets a pattern across the pasted region, restricted to a mask, via `EditSession#setBlocks` |
| `grow` | `GrowEditModifier` | margin | yes | Blends the terrain height map toward a target base level, with directional falloff |
| `smooth` | `SmoothEditModifier` | margin | yes | Gaussian-smooths the terrain height map over N iterations |
| `naturalize` | `NaturalizeEditModifier` | margin | yes | Re-layers grass/dirt/stone via `EditSession#naturalizeCuboidBlocks` |
| `flora` | `FloraEditModifier` | margin | yes | Scatters flora across the surface via WorldEdit's `FloraGenerator` |
| `forest` | `ForestEditModifier` | margin | yes | Grows trees of a given type across the surface via `ForestGenerator` |
| `biome` | `BiomeEditModifier` | margin | yes | Sets the biome across the ring (excluding the footprint) via `BiomeReplace` |
| `deform` | `DeformEditModifier` | margin | yes | Applies a WorldEdit deform expression via `EditSession#deformRegion` |
| `snow` | `SnowEditModifier` | sphere | yes | Simulates snowfall via `EditSession#simulateSnow` over the full-height column |
| `snowIfCold` | `SnowIfColdEditModifier` | sphere | yes | Same as `snow`, but only when the biome temperature at the sphere's centre is ≤ 0.15 |
| `adaptSurface` | `AdaptSurfaceEditModifier` | margin, read-only | no | Replaces the structure's surface blocks with the most common block type sampled from the surrounding columns |
| `thaw` | `ThawEditModifier` | sphere | yes | Melts snow and ice via `EditSession#thaw` over the full-height column |
| `green` | `GreenEditModifier` | sphere | yes | Converts dirt to grass via `EditSession#green` over the full-height column |
| `ex` | `ExtinguishEditModifier` | sphere | no | Removes fire blocks via `EditSession#removeNear` |
| `butcher` | `ButcherEditModifier` | sphere | no | Removes non-player, non-decorative entities (spares armor stands, item frames, paintings) |
| `deltree` | `DelTreeEditModifier` | margin | yes | Removes floating trees — trunks and attached leaves/mushroom blocks not anchored to the ground |
| `dropFloating` | `DropFloatingEditModifier` | margin | yes | Removes plants and similar blocks that lost their supporting ground (grass, flowers, cacti, sugar cane, snow layers, torches, …) |

## Settings grammar

Nine of the eighteen parse their config value as a single space-delimited string via their
own `*Settings` record. Tokens are positional; defaults are shown in parentheses.

- **`adaptSurface`** (`AdaptSurfaceSettings.parse`,
  `structure/src/main/java/me/wyne/wutils/structure/modifier/edit/AdaptSurfaceSettings.java:13-19`):
  `margin(4) mask("") sampleMask(#surface)` — all optional.
- **`biome`** (`BiomeSettings.parse`,
  `structure/src/main/java/me/wyne/wutils/structure/modifier/edit/BiomeSettings.java:16-24`):
  `margin(0) biomeId` — `biomeId` is required; an id without a namespace gets a
  `minecraft:` prefix, and an unknown id throws `IllegalArgumentException`.
- **`deform`** (`DeformSettings.parse`,
  `structure/src/main/java/me/wyne/wutils/structure/modifier/edit/DeformSettings.java:14-19`):
  `margin expression` — split on the **first** run of whitespace only, so the WorldEdit
  expression may itself contain spaces. Unlike every sibling here, `margin` has **no
  default** and empty input throws `NumberFormatException`.
- **`deltree`** / **`dropFloating`** (`DelTreeSettings.parse` /
  `DropFloatingSettings.parse`): `margin(0) includeClipboard(false)`.
- **`flora`** (`FloraSettings.parse`,
  `structure/src/main/java/me/wyne/wutils/structure/modifier/edit/FloraSettings.java:18-24`):
  `margin(0) density(5) includeClipboard(false)` — `density` is a percent chance per column.
- **`forest`** (`ForestSettings.parse`,
  `structure/src/main/java/me/wyne/wutils/structure/modifier/edit/ForestSettings.java:20-30`):
  `margin(0) treeType(tree) density(5) includeClipboard(false)` — `treeType` is resolved via
  WorldEdit's `TreeGenerator.TreeType#lookup`; an unknown name throws
  `IllegalArgumentException`.
- **`grow`** (`GrowSettings.parse`,
  `structure/src/main/java/me/wyne/wutils/structure/modifier/edit/GrowSettings.java:26-46`):
  `margin(5) strength(2) base(+0) [direction] [mask]`. `direction` and `mask` share one
  token slot: the third token is tried as a direction keyword (`up`/`raise`, `down`/`lower`,
  `both`/`slope`); if it matches none of those, it is taken as the mask instead and
  `direction` defaults to `both`. A fourth token, present only when the third *was* a
  direction keyword, supplies the mask.
- **`smooth`** (`SmoothSettings.parse`,
  `structure/src/main/java/me/wyne/wutils/structure/modifier/edit/SmoothSettings.java:13-19`):
  `margin(5) iterations(1) [mask]`.

The other nine keys (`replace`, `set`, `grow`'s siblings `snow`, `snowIfCold`, `thaw`,
`green`, `ex`, `butcher`, `naturalize`) either take a bare number (radius or margin) or a
`mask pattern` pair parsed by `LazyMaskPatternPair` — see [Modifiers](modifiers.md#mask-and-pattern-parsing).

### `includeClipboard`

`includeClipboard` (present on `deltree`, `dropFloating`, `flora`, `forest`) controls
whether the operation is allowed to touch the structure's own footprint. `false` (the
default) makes the modifier's `excludeFootprint()` return `true`, so `MarginEditModifier`
installs the footprint-exclusion mask described above; `true` disables that exclusion and
lets the operation reach into the pasted structure's own columns.

### A delimiter gotcha

`flora` and `forest` parse with `Args`'s **default** colon-or-whitespace delimiter
(`Args.COLON_OR_SPACE_DELIMITER`), while every other settings record in this package uses
`Args.SPACE_DELIMITER` explicitly. A colon anywhere in a `flora`/`forest` value — including
inside a mask-like token — splits there too, unlike its siblings.

## `snow`/`snowIfCold` and the WorldEdit snow bug

`SnowEditModifier` and `SnowIfColdEditModifier`
(`structure/src/main/java/me/wyne/wutils/structure/modifier/edit/SnowEditModifier.java:29-37`,
`SnowIfColdEditModifier.java:33-48`) both catch an `IndexOutOfBoundsException` from
`EditSession#simulateSnow` and log it as a warning instead of letting it propagate. This is
deliberate: it works around a known WorldEdit snow-simulator bug that can throw on certain
block states, so one bad column does not abort the rest of the structure's edits.

## A working example

<!-- allow-code-fences -->
```yaml
my-structure:
  modifiers:
    smooth: 8 2                    # smooth an 8-block ring around the structure, 2 passes
                                    # (also grows the protected region by 8 blocks)
    naturalize: 4                   # re-layer grass/dirt/stone in a 4-block ring
    flora: 4 8 false                 # scatter flora in that same ring, 8% chance per column,
                                      # structure's own footprint excluded
    snowIfCold: 6                     # simulate snowfall in a 6-block sphere, only in cold biomes
    dropFloating: 3 false               # remove now-unsupported plants/blocks in a 3-block ring
    ex: 4                                # extinguish fire within a 4-block sphere
```

## See also

- [Modifiers](modifiers.md) — the `EditSessionModifier` contract, exception handling, and mask/pattern parsing this package builds on.
- [WUtils Structure](structure.md) — the full placement pipeline.
- [Regions and Flags](regions.md) — the WorldGuard region several modifiers here grow.
