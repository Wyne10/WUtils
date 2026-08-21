# Locations and Conditions

A `StructureLocation` (`structure/src/main/java/me/wyne/wutils/structure/location/StructureLocation.java:17`)
supplies a *candidate* placement point for a structure. It is never trusted
as final: `Structure#create` snaps whatever it returns to the highest solid
block (`+1`) and re-validates it against every `LocationCondition` before
using it — a rejected candidate just costs one more iteration of the
generation retry loop (see the pipeline diagram in
[WUtils Structure](structure.md)).

## Choosing a location strategy

`StructureLocation.Factory`
(`structure/src/main/java/me/wyne/wutils/structure/location/StructureLocation.java:35-49`)
dispatches on which keys are present in the config section, in this exact
order — the first match wins:

| Order | Config keys present | Implementation |
|---|---|---|
| 1 | `near-biome`, `far-biome`, or `biome-preset` | `BiomeLocation` |
| 2 | `near-structure` or `far-structure` | `NearestStructureLocation` |
| 3 | `range` | `RandomLocation` |
| 4 | (none of the above) | `SetLocation` |

Because the order is fixed, a section that happens to define both `range`
and `near-biome` becomes a `BiomeLocation` — `range` only matters if no
biome/structure key is present.

<!-- allow-code-fences -->
```yaml
# SetLocation: fixed coordinate, no dynamic search at all
my-structure:
  location:
    world: world
    location: [100, 64, -200]

# RandomLocation: uniform random point in a volume, with an excluded sub-volume
my-structure:
  location:
    world: world
    range: "-500..500, 0..255, -500..500"   # x, y, z ranges
    except: "-50..50, 0..255, -50..50"       # never land inside spawn

# NearestStructureLocation: near (or far from) a vanilla structure
my-structure:
  location:
    world: world
    range: "-2000..2000, 0..255, -2000..2000"
    near-structure: [VILLAGE]
    radius: 200           # search radius passed to World#locateNearestStructure
    near: "10..40"         # distance from the found structure to land at
    find-unexplored: false  # default; true forces a fresh chunk search

# BiomeLocation: near (or far from) a biome, or a whole biome preset
my-structure:
  location:
    world: world
    range: "-5000..5000, 0..255, -5000..5000"
    near-biome: [DESERT, DESERT_HILLS]
    radius: 6400   # default; World#locateNearestBiome search radius
    near: "10..200" # default; distance from the found biome to land at
```

`world`, `range`, and `except` all come from `RandomLocation`'s own config —
`BiomeLocation` and `NearestStructureLocation` each build a `RandomLocation`
internally as their search `origin`
(`structure/src/main/java/me/wyne/wutils/structure/location/BiomeLocation.java:99-101`,
`structure/src/main/java/me/wyne/wutils/structure/location/NearestStructureLocation.java:100-102`),
so those three keys apply to all three dynamic strategies, not just
`RandomLocation` on its own.

## `SetLocation`

`SetLocation` (`structure/src/main/java/me/wyne/wutils/structure/location/SetLocation.java:16`)
is the fallback: a fixed world coordinate, used when a config declares no
dynamic selection strategy at all. `getLocation()` always returns the same
point.

## `RandomLocation`

`RandomLocation` (`structure/src/main/java/me/wyne/wutils/structure/location/RandomLocation.java:23`)
draws a point uniformly at random from `range`, optionally excluding a
sub-volume `except`.

**Sharp edge:** `getLocation()`
(`structure/src/main/java/me/wyne/wutils/structure/location/RandomLocation.java:24-33`)
loops, redrawing, until it lands outside `except`. If `except` covers the
whole `range` — or close enough that a random draw essentially never lands
outside it — this **hangs forever**. There is no attempt limit here, unlike
`BiomeLocation`/`NearestStructureLocation` below.

## `BiomeLocation` and `NearestStructureLocation`

Both follow the same pattern, differing only in what they search for:

1. Draw an origin point from the `RandomLocation` search volume.
2. Locate the nearest matching biome (`World#locateNearestBiome`) or
   structure (`World#locateNearestStructure`) within `radius` of the origin.
   `invert` flips the pool to "any biome/structure type *not* listed".
3. If nothing is found, or nothing was configured to search for (empty
   pool), fall back to the plain origin point.
4. Otherwise, try up to `BOUNDS_ATTEMPTS` = **16**
   (`structure/src/main/java/me/wyne/wutils/structure/location/BiomeLocation.java:37`)
   random points at a `near` distance from what was found
   (`structure/src/main/java/me/wyne/wutils/structure/location/BiomeLocation.java:48-53`,
   `structure/src/main/java/me/wyne/wutils/structure/location/NearestStructureLocation.java:53-58`),
   returning the first one that falls inside the origin `RandomLocation`'s
   bounds (`RandomLocation#withinBounds`,
   `structure/src/main/java/me/wyne/wutils/structure/location/RandomLocation.java:39-43`).
5. If none of the 16 attempts lands in bounds, **this fails silently** — it
   degrades to the plain origin point, not an error. A `near` range set far
   outside `range`'s bounds will quietly and consistently return origin
   points instead of the biome/structure-adjacent points you configured.

Both `World#locateNearestBiome` and `World#locateNearestStructure` are
expensive and Bukkit requires them on the main thread — which is exactly
why `Structure#create` runs the whole location-lookup step via
`Schedulers.sync()` (see [WUtils Structure](structure.md#threading)).

`NearestStructureLocation` additionally exposes `find-unexplored`
(default `false`,
`structure/src/main/java/me/wyne/wutils/structure/location/NearestStructureLocation.java:41`) —
passed straight through to `World#locateNearestStructure`, forcing a search
that may generate new chunks rather than only inspecting already-explored
ones.

## The condition vocabulary

`LocationCondition.FACTORY_MAP`
(`structure/src/main/java/me/wyne/wutils/structure/location/condition/LocationCondition.java:22-47`)
is the full set of keys usable under a structure's shared `conditions`
section:

| Config key | Value type | Implementation |
|---|---|---|
| `is-in-biome` | list of `Biome` names | `BiomeCondition` |
| `is-not-in-biome` | list of `Biome` names | `BiomeCondition` (inverted) |
| `is-in-biome-preset` | list of preset names | `BiomePresetCondition` |
| `is-not-in-biome-preset` | list of preset names | `BiomePresetCondition` (inverted) |
| `is-on-block` | list of `Material` names | `BlockCondition` |
| `is-not-on-block` | list of `Material` names | `BlockCondition` (inverted) |
| `is-in-ocean` | boolean | `OceanCondition` |
| `is-in-mountains` | boolean | `MountainsCondition` |
| `altitude` | comparator string, e.g. `">=64"` | `AltitudeCondition` |
| `temperature` | comparator string, e.g. `"<0.3"` | `TemperatureCondition` |

<!-- allow-code-fences -->
```yaml
my-structure:
  conditions:
    is-in-biome: [PLAINS, SUNFLOWER_PLAINS]
    is-not-on-block: [WATER, LAVA]
    is-in-ocean: false     # must NOT be in an ocean biome
    altitude: ">=64"        # must be at or above y=64
    temperature: "<0.5"      # must be below 0.5 (avoid hot biomes)
```

**Call out the inversion.** `is-in-ocean` and `is-in-mountains` are the two
keys where the config value and the stored flag are opposite:
`OceanCondition`/`MountainsCondition` each store an `invert` field whose
`false` means "must be in" and `true` means "must not be in" — but the
factory negates the raw config boolean when building them
(`structure/src/main/java/me/wyne/wutils/structure/location/condition/LocationCondition.java:37-41`),
so `is-in-ocean: true` in YAML correctly means "must be in ocean" from the
config author's point of view. If you read the source without this in
mind, `OceanCondition(invert=false)` looks backwards until you notice the
factory already flipped it.

`is-in-biome-preset` resolves named biome groups (e.g. `"ocean"`) via
`BiomePreset#resolve`
(`structure/src/main/java/me/wyne/wutils/structure/location/condition/BiomePresetCondition.java:20-22`) —
see [Worlds and Biomes](../common/worlds.md) for what presets exist.
`is-on-block` checks the block **one below** the candidate location
(`structure/src/main/java/me/wyne/wutils/structure/location/condition/BlockCondition.java:24-26`),
i.e. the ground the structure would stand on, not the candidate location's
own block. `is-in-mountains` matches any `Biome` whose name contains
`MOUNTAINS` or `HILLS`, plus `ERODED_BADLANDS`, plus (on server version
1.16.5 specifically) three edge biomes removed in later versions
(`structure/src/main/java/me/wyne/wutils/structure/location/condition/MountainsCondition.java:30-48`).
`altitude` and `temperature` parse comparator strings like `">=64"` or
`"<0.5"` — see [Comparators and Operations](../common/operations.md).

## See also

- [WUtils Structure](structure.md) — where location resolution fits in the overall pipeline, and the threading rules that apply to it.
- [Regions and Flags](regions.md) — `RegionCondition`, the sibling vocabulary evaluated after region modifiers run.
- [Worlds and Biomes](../common/worlds.md) — `BiomePreset`.
- [Comparators and Operations](../common/operations.md) — `IntComparator`/`DoubleComparator` string syntax.
