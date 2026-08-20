# Ranges

The `range` package (`common/src/main/java/me/wyne/wutils/common/range/`) provides bounded
value ranges — integers, doubles, [`TimeSpan`s](durations.md), Bukkit `Vector`s and
world-anchored `Location`s — with a shared shape: a minimum, maximum, center and span, random
sampling, containment testing, and iteration.

Every concrete range extends the abstract
`Range<T>` (`common/src/main/java/me/wyne/wutils/common/range/Range.java:18`), which stores the
four bounds exactly as constructed and declares three abstract operations for subclasses to
define: `getRandom()`, `contains(T)`, and `iterator()` via `Iterable<T>`. `Range` itself does
**not** validate or reorder `min`/`max` — each subclass constructor is responsible for
normalizing its own arguments before calling `super(...)`, and, as covered below, not all of
them do.

Each range type pairs with one or more standalone `Iterator<T>` classes in the `range.iterator`
subpackage. The range class owns construction and semantics (bounds, containment, random
sampling); the iterator classes own traversal and are usable on their own, independent of a
`Range` instance.

## The range types

| Range | Value type | Iterator | `contains()` bounds | `getRandom()` bounds | Iteration order |
|---|---|---|---|---|---|
| `ClosedIntRange` | `Integer` | `ClosedIntRangeIterator` | closed `[min, max]` | closed `[min, max]` | ascending, step 1 |
| `DoubleRange` | `Double` | `DoubleRangeIterator` | closed `[min, max]` | closed `[min, max]` | ascending, step 1.0 |
| `TimeSpanRange` | `TimeSpan` | `TimeSpanIterator` | closed `[min, max]` | closed `[min, max]` | ascending, step 1 second |
| `VectorRange` | `Vector` | `VectorRangeIterator` | closed box, all axes | closed box, all axes | X fastest, Y, Z slowest, step 1.0 |
| `LocationRange` | `Location` (extends `VectorRange`) | `LocationRangeIterator` | closed box, all axes | closed box, all axes | X fastest, Y, Z slowest |

`VectorRange` additionally offers `VectorRangeEdgeIterator` (box wireframe only) as a distinct
iterator obtained by constructing it directly rather than through `VectorRange.iterator()`.
`CircleEdgeIterator` stands apart from this table entirely — it has no owning `Range` subclass
and is built and used on its own.

`ClosedIntRange`, `DoubleRange`, `TimeSpanRange` and `VectorRange` all normalize `min`/`max` in
their two-bound constructor, accepting the arguments in either order. `LocationRange` inherits
that normalization from `VectorRange`. See [Locations and Vectors](locations.md) for
`VectorUtils`, which `VectorRange` uses to normalize per-axis, and for `Location`/`Vector`
conventions generally. See [Durations and Cooldowns](durations.md) for `TimeSpan` and
`Durations`, which back `TimeSpanRange`.

## Bound inclusivity

Every range is closed `[min, max]` in both `contains()` and `getRandom()`. The two families get
there differently, because one is discrete and the other continuous.

**Discrete types add one to the bound.** `ClosedIntRange.getRandom()`
(`ClosedIntRange.java:26`) calls `nextInt(min, max + 1)` and `TimeSpanRange.getRandom()`
(`TimeSpanRange.java:32`) calls `nextLong(min, max + 1)`. Java's integer generators are half-open,
so the `+ 1` is what makes `max` drawable. This is correct and must not be copied to the
continuous types — for a double, `+ 1` would put a whole extra unit of range in play.

**Continuous types route through `Range.randomInclusive`** (`Range.java:46-54`), which is
public so the Kotlin range extensions sample through it too — see
[WUtils Common Kotlin](../commonKt/commonKt.md). It is used by
`DoubleRange.getRandom()` (`DoubleRange.java:23-26`) and per-axis by `VectorRange.getRandom()`
(`VectorRange.java:55-62`), which `LocationRange` inherits and wraps as `getRandomLocation()`
(`LocationRange.java:54-56`). It does two things `ThreadLocalRandom.nextDouble` cannot do alone:

- **A degenerate interval returns its single value.** `nextDouble` requires `origin < bound` and
  throws `IllegalArgumentException` otherwise. This matters far more than it sounds: a
  `VectorRange` samples each axis independently, so *any* flat axis would throw — a region at a
  fixed Y, a line, or a single block. Those are ordinary shapes, and `contains()` accepts them,
  so the two would have disagreed by exception rather than by value.
- **The upper bound is widened by one ulp**, then the draw is clamped to `max`, so `max` is
  genuinely reachable rather than excluded.

Be clear about the scale of that second point. `nextDouble` is continuous, not discrete: over the
interval `1.0..10.0` it draws values like `9.999996792158166`, and roughly 11% of draws land above
`9.0`. The only value the half-open form could never return was exactly `10.0` — one value among
some 2⁶². Widening the bound makes `max` possible in principle; over two million draws you should
still expect to see it zero times. The degenerate guard is the fix that changes observable
behaviour.

## Traversal order

Scalar iterators (`ClosedIntRangeIterator`, `DoubleRangeIterator`, `TimeSpanIterator`) are
linear: ascending when `step > 0`, descending when `step < 0`. A step whose sign doesn't match
the start/end direction yields zero elements. `ClosedIntRangeIterator` and `TimeSpanIterator`
get that by recomputing `hasNext()` against `end` on each call; `DoubleRangeIterator` gets it
from its precomputed element count instead (see [Step arithmetic](#step-arithmetic)).

`VectorRangeIterator` and `LocationRangeIterator` traverse a box as a nested triple loop: X
varies fastest, then Y, then Z varies slowest — equivalent to X as the innermost loop and Z as
the outermost.

`VectorRangeEdgeIterator` does not walk the volume at all. It visits only the box's 12 edges, in
a fixed order: the 4 edges parallel to X first, then the 4 parallel to Y, then the 4 parallel to
Z (`VectorRangeEdgeIterator.java:47-66`). Each edge is a run of `step`-sized points with the
other two axes pinned to one of their min/max combinations, so all 12 edges are covered exactly
once with no edge revisited and no interior or face point ever yielded.

## `CircleEdgeIterator` is not a grid walker

`CircleEdgeIterator` (`common/src/main/java/me/wyne/wutils/common/range/iterator/CircleEdgeIterator.java:26`)
divides a circle into `segments` equal angular steps starting at `startAngle` degrees, computes
each point directly from its angle by trigonometry in the local XY-plane, then applies
yaw/pitch/roll rotation (around Y, then X, then Z, in that order) before translating to
`center`.

There is no block-grid snapping anywhere in this — every yielded point is exactly `radius` away
from `center`, in continuous doubles, angle-discretized only. The `segments` points span
`[startAngle, startAngle + 360 * (segments - 1) / segments)` degrees, so the sequence does not
wrap around to duplicate the starting point.

Construct it directly, or via the nested `Builder` (`CircleEdgeIterator.java:80-143`), which
defaults to a unit circle of 1 segment at the origin with no rotation.

## No instance reuse

Every `Vector`/`Location`-producing iterator in this package — `VectorRangeIterator`,
`VectorRangeEdgeIterator`, `LocationRangeIterator`, `CircleEdgeIterator` — allocates a fresh
instance on each `next()` call and never mutates or reuses a previous one. It is safe to collect
their output into a list, cache it, or hand references out; nothing aliases another point
already returned.

## `VectorRange` construction: normalization is per-constructor, not universal

The two-`Vector` constructor, `VectorRange(Vector min, Vector max)`, normalizes each axis
independently via `VectorUtils.getMin`/`getMax` (`VectorRange.java:31-33`; see
[Locations and Vectors](locations.md)), so `min` and `max` may be passed in either order safely.

The center-based constructors do not:

- `VectorRange(Vector center, double width, double height, double depth)`
  (`VectorRange.java:40-47`) builds `min = center - (w/2, h/2, d/2)` and
  `max = center + (w/2, h/2, d/2)` directly, with no correction.
- `VectorRange(Vector center, double radius)` (`VectorRange.java:49-51`) forwards to the above
  with `width = height = depth = radius`.

A negative width, height, depth, or radius silently produces a box whose stored minimum exceeds
its maximum on that axis. That breaks `contains()` on that axis (the closed-interval check
`value >= min && value <= max` can never be satisfied) and feeds directly into the
empty-iteration behaviour above if the box is iterated.

`LocationRange`'s single-`double` constructors (`LocationRange(Location, double, double, double)`
and `LocationRange(Location, double)`) forward straight into these same `VectorRange`
constructors, so they carry the identical risk.

### `radius` names a box, not a sphere

Despite the parameter name, `VectorRange(center, radius)` and `LocationRange(center, radius)`
both build an axis-aligned cube extending `radius` in every direction from `center` — not a
sphere. `contains()` on either is the inherited box check, not a distance check. A caller
expecting sphere semantics (e.g. "is this point within `radius` blocks of center") gets a cube
and will see corners return `true` where a sphere would have returned `false`.

## `LocationRange`: world binding and null risk

`LocationRange` extends `VectorRange`, adding a `World` and delegating containment,
random-point, and iteration behavior to the inherited box logic — `getRandomLocation()` wraps
`VectorRange.getRandom()`'s `Vector` into a `Location` via
[`LocationUtils`](locations.md), and `contains(Location)` first checks the location's world is
identity-equal to the range's world before delegating to the vector check
(`LocationRange.java:58-61`).

`getWorld()` is annotated non-null, but two code paths can hand it a null `World` at runtime:

- `LocationRange.getLocationRange(String)` passes a raw world name to `Bukkit.getWorld(name)`
  (`LocationRange.java:91-94`); an unloaded or misspelled world name resolves to `null`.
- The `Location`-based constructors read `location.getWorld()`
  (`LocationRange.java:37-40`), which Bukkit itself documents as nullable for a `Location` not
  currently associated with a loaded world.

Either path produces a `LocationRange` whose `@NotNull getWorld()` lies about its own return
value. Code parsing world names from config, or building ranges from stored/deserialized
locations, should check for this before trusting the annotation. See
[Worlds and Biomes](worlds.md) for more on world lookup and loading.

## Building ranges from strings

Every range type except `LocationRange` (which composes `VectorRange`'s parser) exposes a static
`get*Range(String)` factory, used to load ranges out of config values. All of them split on the
literal string `..` and throw an unchecked parsing exception (`NumberFormatException` or an
`ArrayIndexOutOfBoundsException` from the missing second half) on malformed input — none of them
validate or catch.

| Factory | Format | Notes |
|---|---|---|
| `ClosedIntRange.getIntRange` (`ClosedIntRange.java:47-50`) | `min..max` | each side parsed with `Integer.parseInt` |
| `DoubleRange.getDoubleRange` (`DoubleRange.java:46-49`) | `min..max` | each side parsed with `Double.parseDouble` |
| `TimeSpanRange.getTimeSpanRange` (`TimeSpanRange.java:54-57`) | `min..max` | each side parsed by `Durations.getTimeSpan` — see [Durations and Cooldowns](durations.md) for the duration string syntax |
| `VectorRange.getVectorRange` (`VectorRange.java:83-86`) | `minX,minY,minZ..maxX,maxY,maxZ` | each half parsed by `VectorUtils.getVectorOrZero`, which defaults any blank axis to `0` |
| `LocationRange.getLocationRange` (`LocationRange.java:91-94`) | `world minX,minY,minZ..maxX,maxY,maxZ` | split on whitespace/colon via `Args`; the world half is resolved with `Bukkit.getWorld`, subject to the null-world risk above; the coordinate half delegates to `VectorRange.getVectorRange` |

Because the two-`Vector` `VectorRange` constructor normalizes per-axis, a string-built
`VectorRange`/`LocationRange` is always a valid, non-inverted box regardless of which corner is
written first in config — the normalization risk described above applies only to the
dimension/radius constructors, not to the string parsers.

## See also

- [WUtils Common](common.md) for the module overview and how `range` fits alongside the rest of
  `common`.
- [Durations and Cooldowns](durations.md) for `TimeSpan` and `Durations`, which back
  `TimeSpanRange`.
- [Locations and Vectors](locations.md) for `LocationUtils`/`VectorUtils`, which back
  `LocationRange`/`VectorRange`.
- [Worlds and Biomes](worlds.md) for world resolution, relevant to `LocationRange`'s null-world
  risk.
- [Vendored helper Library](helper.md) — unrelated to `range`, but the module-wide note on what
  is and isn't first-party WUtils code.
