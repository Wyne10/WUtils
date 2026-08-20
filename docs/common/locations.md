# Locations and Vectors

Two small packages that mirror each other: `location/` (`LocationUtils`) and `vector/`
(`VectorUtils`). Both provide the same relative-offset math — one operating on Bukkit `Location`s,
the other on `Vector`s — so the two pages are combined here.

## LocationUtils.of

`of(World, Vector)` and `of(World, Vector, Vector direction)`
(`common/src/main/java/me/wyne/wutils/common/location/LocationUtils.java:21-30`) build a
`Location` in `world` from a `Vector`'s coordinates, optionally also setting a facing direction.

## VectorUtils.zero / getVector

`zero()` (`common/src/main/java/me/wyne/wutils/common/vector/VectorUtils.java:16-18`) returns a
**new** zero `Vector` on every call — not a shared constant — so callers can mutate the result
freely (e.g. via `.add(...)`) without corrupting a shared instance.

`getVector(String string, Vector def)`
(`common/src/main/java/me/wyne/wutils/common/vector/VectorUtils.java:24-30`) parses a `"x,y,z"`
string into a `Vector`, using `Args` (see [Core Utilities](utilities.md)) to split on commas.
Any axis left blank in the string falls back to the matching component of `def`.
`getVectorOrZero(String)` is the same with `def = zero()`.

## VectorUtils.getMin / getMax / isEmpty

`getMin`/`getMax` (`common/src/main/java/me/wyne/wutils/common/vector/VectorUtils.java:38-53`)
return a new vector with the component-wise minimum/maximum of two inputs — neither argument is
mutated. `isEmpty(Vector)`
(`common/src/main/java/me/wyne/wutils/common/vector/VectorUtils.java:56-58`) is `true` only when
all three components are exactly `0.0`.

## addRelative

Both `LocationUtils` and `VectorUtils` expose the same four-shape family of `addRelative`
overloads for offsetting a point relative to a facing direction rather than to world axes:

- **Horizontal/vertical + `BlockFace`** — `addRelative(_, double horizontal, double vertical,
  BlockFace face)`. Rotates the horizontal component onto whichever world axis `face` is *not*
  aligned with (e.g. facing `NORTH`/`SOUTH` puts `horizontal` on X; facing `EAST`/`WEST` puts it on
  Z).
- **Width/height/depth + `BlockFace`** — `addRelative(_, double width, double height, double
  depth, BlockFace face)`. Swaps `width` and `depth` depending on which axis `face` runs along, so
  the box stays oriented relative to `face`.
- **`Vector relativeOffset` + `BlockFace`/`Vector forward`** — the general case. Treats
  `relativeOffset` as `(x = right, y = up, z = forward)` and rotates it into world space using the
  cross product of `forward` and world-up, so `z` always points along `forward`. Returns an
  unmodified clone of the base point if `relativeOffset` is empty (`VectorUtils.isEmpty`).
- `LocationUtils.addRelative(Location, Vector relativeOffset)` (no face/forward argument) uses the
  location's own current direction as `forward`.

All overloads return a new/cloned instance rather than mutating the input. See
`common/src/main/java/me/wyne/wutils/common/location/LocationUtils.java:38-84` and
`common/src/main/java/me/wyne/wutils/common/vector/VectorUtils.java:66-107`.

## LocationUtils.getRandomPointNear

`getRandomPointNear(Location center, int radius)`
(`common/src/main/java/me/wyne/wutils/common/location/LocationUtils.java:87-92`) picks a uniformly
random point on the circle of `radius` around `center`, at the same Y coordinate, using
`ThreadLocalRandom`. Coordinates are rounded to whole blocks.

## See also

- [Core Utilities](utilities.md) — `Args`, the delimiter parser `getVector` uses.
- [Worlds and Biomes](worlds.md) — uses `VectorUtils.zero()` and vector ranges for chunk-local
  coordinates.
