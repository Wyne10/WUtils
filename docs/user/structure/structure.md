---
description: >-
  Search for a spot that satisfies your conditions, paste a WorldEdit clipboard there,
  and protect it with a WorldGuard region — reversibly.
---

# Structures

`wutils-structure` places schematic-based structures in a Bukkit/Paper world: it searches
for a spot that satisfies conditions you configure, pastes a WorldEdit clipboard there,
and protects the pasted area with a WorldGuard region — reversibly, so you can tear a
structure back down and restore the original terrain. Reach for it for anything you want
generated procedurally into an existing world (loot rooms, random dungeons, world-gen
add-ons); for a structure you place by hand once and never move, plain WorldEdit/
WorldGuard is simpler.

Everything is driven from YAML. You write a `Structure` recipe in config; running it
produces a `WorldStructure`, the placed instance you interact with at runtime.

**This module requires both WorldEdit and WorldGuard.** Neither is optional — every
clipboard, region, mask, and pattern operation goes through one or the other, and a
consumer missing either one hits a runtime `NoClassDefFoundError`, not a build failure,
the first time a structure is generated.

## Adding it to your build

```kotlin
dependencies {
    implementation("io.github.wyne10:wutils-structure:1.2.8")
}
```

| Dependency | Scope | Notes |
|---|---|---|
| WorldEdit (`worldedit-bukkit` 7.2.17+) | `compileOnlyApi` — **you must supply this** | clipboards, regions, pastes, masks, patterns |
| WorldGuard (`worldguard-bukkit` 7.0.5+) | `compileOnlyApi` — **you must supply this** | protection regions, flags, region conditions |
| Paper API 1.16.5 | `compileOnly` | consumer supplies a Paper (or Paper-fork) runtime |

`wutils-common` and `wutils-configurables` come along transitively — you don't add them
yourself. Gson (needed for [persistence](#surviving-a-restart)) arrives transitively
through WorldEdit's own dependencies; you don't declare it either.

## The two-phase model

A `Structure` only describes the recipe — it holds no world state by itself. Calling
`create(...)` on it searches for a valid placement and, once found, yields a
`WorldStructure`: a resolved but not-yet-placed instance. Calling `spawn()` on that
actually pastes the clipboard and registers the protection region. Calling `close()`
reverses `spawn()` — it unregisters the region and repastes whatever was there before,
undoing the placement. It also makes the server rescan any column whose height map still
reports air, which FastAsyncWorldEdit can leave behind, so later highest-block lookups in
that area stay correct.

```mermaid
flowchart LR
    A["Structure\n(YAML recipe)"] -- "create(...)" --> B["WorldStructure\n(resolved, not placed)"]
    B -- "spawn()" --> C["placed in the world"]
    C -- "close()" --> D["rolled back"]
```

Because `close()` cleanly undoes `spawn()`, try-with-resources is a working pattern for a
temporary structure — it's placed on entry and rolled back on exit:

```java
try (WorldStructure worldStructure = structure.create(5000, null, ForkJoinPool.commonPool())
        .get()) {
    worldStructure.spawn();
    // ... the structure exists in the world for the rest of this block ...
} // close() runs automatically here: region unregistered, terrain restored
```

(`.get()` blocks the calling thread and throws the checked `InterruptedException`/
`ExecutionException` — fine to block on off the main thread, not on it; prefer
`.thenAccept(...)` as in the example below when you're already on the main thread.)

For a structure meant to persist, don't use try-with-resources — hold onto the
`WorldStructure` (or [persist it](#surviving-a-restart)) and call `close()` only when you
actually want it removed.

## A complete example

```yaml
goblin-camp:
  location:
    world: world
    range: "-2000..2000, 0..255, -2000..2000"
    except: "-100..100, 0..255, -100..100"   # never land near spawn
  scheme:
    schemes: schematics/goblin-camp-.*\.schem  # picks one at random matching this regex
  region:
    id: goblin-camp-<x>x<y>y<z>z
    margin: 4
    flags:
      pvp: allow
      mob-spawning: allow
  conditions:
    is-in-biome: [FOREST, PLAINS]
    is-not-on-block: [WATER, LAVA]
    altitude: ">=60"
    altitude-difference: "<=6"          # reject overly uneven terrain
  modifiers:
    rotate: any                          # random 90-degree rotation each placement
    outset: 6 -h                          # grow the protected region 6 blocks, horizontal only
    smooth: 8 2                            # smooth an 8-block ring around it, 2 passes
    snapshotEntities: true                  # rollback restores entities too
    pasteEntities: true                      # paste copies entities baked into the schematic
```

Loading and running it:

```java
ConfigurationSection section = config.getConfigurationSection("structures.goblin-camp");
Structure structure = new Structure(section);

structure.create(10_000, null, ForkJoinPool.commonPool())
        .thenAccept(worldStructure -> Bukkit.getScheduler().runTask(plugin, worldStructure::spawn))
        .exceptionally(ex -> {
            plugin.getLogger().warning("Could not place goblin-camp: " + ex.getMessage());
            return null;
        });
```

`create(...)` runs the search off the main thread except for the parts Bukkit requires on
it (world height and biome/structure lookups); `spawn()` and `close()` are **not**
scheduled by this module at all — always call them yourself on the main thread, since
WorldEdit/WorldGuard expect that.

The `10_000` is a total millisecond budget across every retry, not per attempt — running
out fails the future with `IllegalStateException`. Pass a `StructureCancellationToken`
instead of `null` if you want to be able to cancel a long-running search yourself; a
cancelled token fails the future with `CancellationException` instead, so you can tell the
two failure modes apart.

## Location strategies

Which strategy you get is decided by which config keys are present under `location:`,
checked in this order — the first match wins:

| Keys present | Strategy | What it does |
|---|---|---|
| `near-biome` / `far-biome` / `biome-preset` | `BiomeLocation` | Finds a point near (or far from) a biome or [named biome group](#biome-presets) |
| `near-structure` / `far-structure` | `NearestStructureLocation` | Finds a point near (or far from) a vanilla structure |
| `range` | `RandomLocation` | Uniform random point inside a volume, optionally excluding a sub-volume via `except` |
| (none of the above) | `SetLocation` | A single fixed coordinate — no search at all |

`world`, `range`, and `except` apply to all three dynamic strategies (`BiomeLocation` and
`NearestStructureLocation` both search from a `RandomLocation` internally), not just
`RandomLocation` on its own. A candidate location is never trusted as final — it's
snapped to the highest solid block and checked against every condition before being used.

## Conditions

Conditions live in one shared `conditions:` section and can be freely interleaved —
location conditions are checked right after a candidate location is found; region
conditions are checked after region modifiers have reshaped the protected region, so they
see its *final* shape, margin/expansion included.

| Config key | Checks | Stage |
|---|---|---|
| `is-in-biome` / `is-not-in-biome` | biome name list | location |
| `is-in-biome-preset` / `is-not-in-biome-preset` | [named biome group](#biome-presets) | location |
| `is-on-block` / `is-not-on-block` | material of the block **one below** the candidate | location |
| `is-in-ocean` | ocean biome | location |
| `is-in-mountains` | mountain/hill-family biome | location |
| `altitude` | comparator on Y, e.g. `">=64"` | location |
| `temperature` | comparator on biome temperature, e.g. `"<0.5"` | location |
| `region-whitelist` | every WorldGuard region overlapping the candidate must match one of these regexes | region |
| `top-region-whitelist` | the highest-priority overlapping region must match one of these regexes | region |
| `region-blacklist` | no WorldGuard region overlapping the candidate may match any of these regexes | region |
| `top-region-blacklist` | the highest-priority overlapping region must not match any of these regexes | region |
| `altitude-difference` | comparator on terrain height variance under the footprint, e.g. `"<=8"` | region |

Failing any condition — location or region — restarts the whole search from a fresh
candidate location, not just the failed step.

### Biome presets

`biome-preset` under `location:`, and `is-in-biome-preset`/`is-not-in-biome-preset` under
`conditions:`, take a list of preset names:

```yaml
conditions:
  is-in-biome-preset: [SNOWY_OR_COLD, "!OCEAN"]   # cold biomes, minus the frozen oceans
```

Entries apply in list order: a plain name adds that preset's biomes and a `!` prefix
removes them. Quote any `!` entry, since a bare `!` means something else to YAML. Names
are case-insensitive, and an unknown name is skipped without an error.

The presets are `ALL`, `SNOWY`, `COLD`, `SNOWY_OR_COLD`, `LUSH`, `WARM`, `LUSH_OR_WARM`,
`OCEAN`, `HIGHLAND`, `WOODLAND`, `WETLAND`, `NETHER`, `END` and `OVERWORLD`. See
[the exact biomes in each preset](../common/game-objects.md#biome-presets).

## The modifier pipeline

Modifiers are config-driven hooks that run at fixed points regardless of what order you
write them in YAML — see [the surprise below](#registration-order-not-yaml-order). Six
interfaces cover the whole pipeline:

| Stage | Interface | Runs | Does |
|---|---|---|---|
| 1 | `ClipboardModifier` | before location/region are computed | Picks the paste rotation/flip |
| 2 | `LocationModifier` | after the highest-block lookup | Nudges the placement Y (or point) |
| 3 | `RegionModifier` | before region conditions | Reshapes the protected region (expand/contract/outset/inset) |
| 4 | `SnapshotModifier` | during `spawn()`, before the rollback snapshot | Configures what the pre-paste snapshot captures |
| 5 | `PasteModifier` | during `spawn()`, after the snapshot | Configures the paste itself (entities, biomes, masks) |
| 6 | `EditSessionModifier` | during `spawn()`, after the paste | Edits blocks, biomes or entities: inside the pasted structure, around it, or both, depending on the key (see [where terrain edits land](#where-terrain-edits-land)) |

Stages 1–3 run inside `create(...)`: if one throws, the whole placement attempt aborts.
Stages 4–6 run inside `spawn()`: if one throws, it's logged and skipped — one bad modifier
doesn't sink the paste or the others.

### All modifier keys

Leaving a key out turns that modifier off, so the defaults below only matter once you've
written the key. The exceptions are the boolean and mask keys in stages 4 and 5, where
leaving the key out keeps the WorldEdit behavior listed under **Default**.

Values are positional tokens separated by spaces. `name(x)` means the token falls back to
`x` if you stop writing before it, `[name]` is optional with no default, and a plain name
is required. To set a later token you have to write the ones before it.

**Always write the first token.** An empty value doesn't select the defaults: `smooth: ""`
fails with a `NumberFormatException`. Write `smooth: 5` instead. `rotate` and `flip` are
the exceptions.

| Config key | What it changes | Value | Default | Stage |
|---|---|---|---|---|
| `rotate` | the paste's rotation | angles that are multiples of 90 (`90 180`), one picked per placement | `0 90 180 270` for empty, `any`, `random` or `true` | 1 |
| `flip` | the paste's mirroring | axes (`x`, `y`, `z`, `none`), one picked per placement | `none x z` for empty, `any`, `random` or `true` | 1 |
| `altitude` | the placement Y | an [`IntOperation`](/contributing/common/operations) string, e.g. `+2` | Y is left unchanged | 2 |
| `expand` / `contract` | one face of the protected region | `direction amount(1)` | no `direction` default | 3 |
| `outset` / `inset` | all faces of the protected region | `amount(1) [-h] [-v]` | no flag, or both, applies to both axes | 3 |
| `snapshotEntities` | whether the rollback snapshot captures entities | boolean | `true` (WorldEdit default) | 4 |
| `snapshotRemoveEntities` | whether taking the snapshot removes entities from the source | boolean | `false` | 4 |
| `snapshotBiomes` | whether the rollback snapshot captures biomes | boolean | `false` | 4 |
| `snapshotSourceMask` | which blocks the snapshot captures | mask string | every block in the region | 4 |
| `pasteEntities` | whether the paste copies the clipboard's entities | boolean | `true` (WorldEdit default) | 5 |
| `pasteBiomes` | whether the paste copies the clipboard's biomes | boolean | `false` | 5 |
| `pasteIgnoreAir` | whether clipboard air blocks are skipped | boolean | `false` | 5 |
| `pasteSourceMask` | which clipboard blocks are pasted | mask string | every clipboard block | 5 |

A boolean key whose value isn't a boolean reads as `false`, not as the default above.

### Where terrain edits land

Stage 6 modifiers don't all work in the same place. The **pasted box** below is the
bounding box the structure occupies in the world.

| Writes to | Meaning |
|---|---|
| **Inside** | Only inside the pasted box. Nothing around it changes. |
| **Outside** | Only around the pasted box, up to `margin` blocks out on every side, including above and below. The box itself is never written to. |
| **Outside or both** | **Outside** by default. Setting `includeClipboard` to `true` also lets it work inside the box. |
| **Both** | Everything within reach of the box's center, inside and outside alike. |

| Config key | What it does | Value | Writes to |
|---|---|---|---|
| `replace` | replace blocks matching a mask with a pattern | `mask pattern` | **Inside** |
| `set` | set a pattern on blocks matching a mask | `mask pattern` | **Inside** |
| `deform` | apply a WorldEdit deform expression | `margin expression` | **Outside** |
| `grow` | blend terrain height toward a target level | `margin(5) strength(2) base(+0) direction(both) [mask]` | **Outside** |
| `smooth` | Gaussian-smooth terrain height | `margin(5) iterations(1) [mask]` | **Outside** |
| `naturalize` | re-layer grass/dirt/stone | `margin` | **Outside** |
| `adaptSurface` | replace the structure's blocks matching `mask` with the surrounding area's most common surface block | `margin(4) mask sampleMask(#surface)` | **Inside** (`margin` only sets how far out it samples, and it never writes there) |
| `green` | convert dirt to grass | `radius` | **Both**, in a circle |
| `biome` | set the biome around the structure | `margin(0) biomeId` | **Outside** |
| `flora` | scatter flora | `margin(0) density(5) includeClipboard(false)` | **Outside or both** |
| `forest` | grow trees | `margin(0) treeType(tree) density(5) includeClipboard(false)` | **Outside or both** |
| `thaw` | melt snow/ice | `radius` | **Both**, in a circle |
| `snow` / `snowIfCold` | simulate snowfall (`snowIfCold` only when the temperature at the center is at most 0.15) | `radius` | **Both**, in a circle |
| `ex` | extinguish fire | `radius` (at least 1) | **Both**, in a cube reaching `radius - 1` blocks from the center |
| `butcher` | remove entities, except players, armor stands, item frames and paintings | `radius` | **Both**, in a sphere, entities only |
| `deltree` | remove floating trees | `margin(0) includeClipboard(false)` | **Outside or both** |
| `dropFloating` | remove now-unsupported plants/blocks | `margin(0) includeClipboard(false)` | **Outside or both** |

A few consequences are easy to miss:

- **A margin of 0 can mean no edit at all.** `biome` never writes inside the box, so
  `biome: 0 plains` does nothing. The same goes for `flora`, `forest`, `deltree` and
  `dropFloating` at margin 0, unless `includeClipboard` is `true`.
- **`radius` is measured from the center, not the edge.** On a 30-block-wide structure,
  `thaw: 4` only reaches the middle of the roof and never gets outside. Use a radius
  larger than half the structure's width to reach the surroundings.
- **`green`, `thaw`, `snow` and `snowIfCold` only touch the top of each column.** A roof
  gets snow, the rooms underneath don't.
- **Only `flora` and `forest` skip the whole column over the structure.** The other
  **Outside** keys can still write directly above or below the pasted box, within
  `margin`.

Full grammar for each key — including the `RegionRadiusEditModifier`/`MarginEditModifier`
distinction and every settings parser's exact token order — is in the contributor wiki's
[Modifiers](/contributing/structure/modifiers) and
[Terrain Edit Modifiers](/contributing/structure/edit-modifiers) pages.

## Two things that will surprise you

**Registration order is application order, regardless of YAML key order.** Modifiers
always run in a fixed internal order — the one in the tables above — no matter what order
you write them under `modifiers:` in your file. Writing `smooth` before `rotate` in your
YAML does not make smoothing happen first; rotation (stage 1) always happens before any
terrain edit (stage 6).

**13 of the 18 terrain-edit modifiers silently grow your WorldGuard region.** Adding
`smooth: 8 2` (or `grow`, `naturalize`, `flora`, `forest`, `biome`, `deform`, `snow`,
`snowIfCold`, `thaw`, `green`, `deltree`, `dropFloating`) to a structure doesn't just edit
terrain — it also expands the protected region to cover the area it edited, the same way
`outset` would. Only `replace`, `set`, `adaptSurface`, `ex`, and `butcher` leave the region
untouched. If your protection region ends up bigger than your `region:` section implies,
check whether one of these is why.

## Surviving a restart

A spawned `WorldStructure` only holds its clipboard and rollback snapshot in memory — on
a server restart, that state is gone even though the structure is still pasted in the
world, and there is no clean way to `close()` it anymore. `WorldStructurePersistence`
writes a spawned structure's full state (as JSON plus two `.schem` files) to disk so it
survives:

```java
WorldStructurePersistence persistence = new WorldStructurePersistence(schematicDirectory);

persistence.save(new File(dataFolder, "goblin-camp.json"), worldStructure); // after spawn()

// ... later, after the target world has finished loading on the next startup ...
WorldStructure restored = persistence.load(new File(dataFolder, "goblin-camp.json"));
restored.close(); // rolls it back exactly as if it had never left memory
```

Both `save(...)` and `load(...)` declare `throws IOException` — wrap or propagate as your
own plugin's error handling expects.

`load(...)` does **not** re-spawn the structure — the paste already exists in the world
from before the restart — it only gives you back a handle you can `close()`. See the
contributor wiki's [Persistence](/contributing/structure/persistence) page for the memento
format and the two schematic files it writes.

## See also

- [the contributor wiki](/contributing/structure/structure) — the full generation
  pipeline, threading rules, and the programmatic `Structure.Builder`.
- [Schemes and Clipboards](/contributing/structure/schemes) — how `scheme`/`schemes`
  resolve to a clipboard, including the directory-plus-regex gotcha in `schemes`.
- [Locations and Conditions](/contributing/structure/locations) — every location
  strategy's exact search algorithm, including `RandomLocation`'s infinite-loop hazard if
  `except` covers all of `range`.
- [Regions and Flags](/contributing/structure/regions) — `RegionData`, WorldGuard flag
  resolution, and region id sanitization.
- [Modifiers](/contributing/structure/modifiers) and
  [Terrain Edit Modifiers](/contributing/structure/edit-modifiers) — full grammar for every
  modifier key in the tables above.
- [Configurables](../configurables/configurables.md) — the `AttributeMap`/
  `AttributeConfigurable` machinery `Structure` and its modifiers are built on.
