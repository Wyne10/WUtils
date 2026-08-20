# Worlds and Biomes

The `world/` package (`common/src/main/java/me/wyne/wutils/common/world/`) covers two things:
async highest-block lookups plus chunk-local coordinate ranges (`WorldUtils`), and named biome
groupings for config-friendly filtering (`BiomePreset`).

## WorldUtils — highest-block lookups

`getHighestBlockAtAsync` / `getHighestLocationAtAsync` come in eight overloads, all converging on
the `(World, int x, int z)` pair
(`common/src/main/java/me/wyne/wutils/common/world/WorldUtils.java:36-45`): variants taking
`double` coordinates (floored via `NumberConversions.floor`), a `Vector` (using its block
coordinates), or a `Location` (using its world and block coordinates).

Each loads the target chunk via `World.getChunkAtAsync(x >> 4, z >> 4)` first, then resolves the
future to `World.getHighestBlockAt(x, z)` (or its `Location`). It is safe to call from off the main
thread; per Bukkit's `getChunkAtAsync` contract, the completion callback itself still runs on the
**server main thread**, so code chained onto the returned `CompletableFuture` can safely touch
Bukkit API without hopping threads again.

**Sharp edge:** the `Location`-based overloads
(`common/src/main/java/me/wyne/wutils/common/world/WorldUtils.java:68-75`) pass
`location.getWorld()` straight into the `@NotNull World world` parameter of the `(World, int, int)`
overload. Bukkit allows `Location.getWorld()` to return `null` (e.g. for a location whose world was
unloaded). Passing such a location does **not** fail at the call site — it throws a
`NullPointerException` inside `WorldUtils` itself, once `world.getChunkAtAsync(...)` is invoked on
the null reference. Callers passing a `Location` should ensure its world is non-null first.

## WorldUtils — chunk coordinate ranges

| Constant | Meaning |
|---|---|
| `CHUNK_X_RANGE`, `CHUNK_Z_RANGE` | `ClosedIntRange(0, 15)` |
| `CHUNK_Y_RANGE` | `ClosedIntRange(0, 255)` |
| `CHUNK_RANGE` | a `VectorRange` spanning the full in-chunk volume, `(0,0,0)` to `(15,255,15)` |

See `common/src/main/java/me/wyne/wutils/common/world/WorldUtils.java:25-29`, and
[Ranges](ranges.md) for `ClosedIntRange`/`VectorRange` themselves.

`getRandomChunkPoint()` (`common/src/main/java/me/wyne/wutils/common/world/WorldUtils.java:78-80`)
returns a uniformly random point within `CHUNK_RANGE` on all three axes.
`getRandomChunkPoint2D()` (`common/src/main/java/me/wyne/wutils/common/world/WorldUtils.java:83-85`)
does the same for X/Z only, with Y fixed at `0`.

`HIGHLAND_REGEX`
(`common/src/main/java/me/wyne/wutils/common/world/WorldUtils.java:23`) matches biome enum names
describing elevated terrain (`MOUNTAINS`, `HILLS`, `PEAKS`, `SLOPES`, `PLATEAU`, `ERODED`); it
backs `BiomePreset.HIGHLAND` below.

## BiomePreset

`BiomePreset` (`common/src/main/java/me/wyne/wutils/common/world/BiomePreset.java`) is an enum
where each constant is a named, precomputed `Set<Biome>` — for config values that want to refer to
"cold biomes" or "ocean biomes" rather than enumerate every `Biome` constant by hand.

**Biome-name resolution:** presets built from string lists (all except `ALL`, `SNOWY_OR_COLD`,
`LUSH_OR_WARM`, `OCEAN`, `HIGHLAND`, `OVERWORLD`, which are computed from `Biome.values()` or other
presets) resolve each name via `Biome.valueOf(name)`
(`common/src/main/java/me/wyne/wutils/common/world/BiomePreset.java:78-89`). A name that doesn't
exist on the running server version (older/newer Minecraft biome sets differ) is **silently
dropped** rather than throwing — the preset just ends up with fewer biomes.

Presets:

| Preset | Basis |
|---|---|
| `ALL` | every `Biome` except `MOUNTAIN_EDGE`, `THE_VOID`, `CUSTOM` |
| `SNOWY` | explicit snowy/frozen biome list |
| `COLD` | explicit mountain/taiga/frozen-ocean list |
| `SNOWY_OR_COLD` | union of `SNOWY` and `COLD` |
| `LUSH` | explicit plains/forest/jungle/swamp/river/beach list |
| `WARM` | explicit desert/savanna/badlands list |
| `LUSH_OR_WARM` | union of `LUSH` and `WARM` |
| `OCEAN` | every `Biome` whose name contains `"OCEAN"` |
| `HIGHLAND` | every `Biome` matching `HIGHLAND_REGEX` |
| `WOODLAND` | explicit forest/taiga/jungle/wooded list |
| `WETLAND` | explicit river/swamp/beach/shore list |
| `NETHER` | the five Nether biomes |
| `END` | the five End biomes |
| `OVERWORLD` | `ALL` minus `NETHER` minus `END` |

`getBiomes()` (`common/src/main/java/me/wyne/wutils/common/world/BiomePreset.java:92-94`) returns
a preset's resolved, unmodifiable `Set<Biome>`.

### BiomePreset.resolve

`resolve(List<String> presets)`
(`common/src/main/java/me/wyne/wutils/common/world/BiomePreset.java:101-118`) turns a config-style
list of preset names into one combined `Set<Biome>`, processed in order:

- a plain name (e.g. `"COLD"`) adds that preset's biomes to the running result;
- a `!`-prefixed name (e.g. `"!SNOWY"`) removes that preset's biomes from the running result;
- matching is case-insensitive (`toUpperCase(Locale.ENGLISH)`) against the enum's constant names;
- an unknown preset name (doesn't match any `BiomePreset` constant) is silently skipped.

Because entries apply in list order, a later negation can undo an earlier addition (or vice
versa) — the final set depends on the order entries are given in, not just which ones are present.

## See also

- [Ranges](ranges.md) — `ClosedIntRange`, `VectorRange`, `Range`.
- [Locations and Vectors](locations.md) — `VectorUtils.zero()`, used to build `CHUNK_RANGE`.
