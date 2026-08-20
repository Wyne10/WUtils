# WUtils Common Kotlin

`wutils-common-kotlin` is a thin Kotlin ergonomics layer over
[`common`](../common/common.md). Almost every declaration in it takes a static Java
helper whose first parameter is the subject — `ItemUtils.isNullOrAir(stack)` — and
re-expresses it as an extension on that subject: `stack.isNullOrAir()`.

It adds very little behaviour of its own. What it adds instead is call-site readability,
Kotlin-native types (`IntRange`, `ClosedFloatingPointRange`, nullable returns), and
`reified` generics that remove the `Class<E>` argument from the enum helpers.

- **Artifact**: `io.github.wyne10:wutils-common-kotlin`
- **Root package**: `me.wyne.wutils.common.kotlin`
- **Version**: inherited from `:WUtils-common` (`commonKt/build.gradle.kts:23`), so the
  two always ship in lockstep.
- **Size**: 14 files, ~480 lines. There is no class in the module — it is entirely
  top-level extension functions, extension properties and typealiases.

## Dependencies

| Dependency | Scope | Notes |
|---|---|---|
| `:WUtils-common` | `api` | Re-exported: depending on this module gives you `common` too, with no separate declaration. |
| `org.jetbrains.kotlin:kotlin-stdlib:2.4.10` | `compileOnly` | **Not bundled.** The consuming plugin supplies the stdlib — normal for a Kotlin plugin, but the artifact is unusable from a project without one. |
| `com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT` | `compileOnly` | For the Bukkit receiver types. |
| `dev.jorel:commandapi-bukkit-core:9.4.2` | `compileOnly` | Only `command/CommandUtils.kt`. |

The module targets `jvmToolchain(16)` (`commonKt/build.gradle.kts:6-8`), matching the
Java level used across the repo.

## Layout

One file per subject, each mirroring the `common` package it wraps:

| File | Wraps | Adds beyond delegation |
|---|---|---|
| `block/BlockUtils.kt` | [Blocks](../common/blocks.md) | `BlockFace.yaw` property |
| `command/CommandUtils.kt` | [Commands](../common/commands.md) | `suggest`/`tooltip` builders |
| `config/ConfigUtils.kt` | [Config Utilities](../common/config-utils.md) | `reified` enum reads, Kotlin-range reads, `Material` reads |
| `doubles/DoubleUtils.kt` | — | `Double.test()`, a probability roll |
| `inventory/InventoryUtils.kt` | [Inventories](../common/inventories.md) | nothing — pure delegation |
| `item/ItemUtils.kt` | [Items](../common/items.md) | the `fuelTicks` table |
| `location/LocationUtils.kt` | [Locations and Vectors](../common/locations.md) | `locationOf` free functions |
| `player/PlayerUtils.kt` | [Players](../common/players.md) | properties for exp accessors |
| `random/RandomUtils.kt` | `RandomUtils` | nullable `weightedRandom` property |
| `range/RangeUtils.kt` | [Ranges](../common/ranges.md) | conversions to/from Kotlin ranges |
| `server/ServerUtils.kt` | [Plugin Composition](../common/plugin.md) | `Server.currentVersion` |
| `sound/SoundUtils.kt` | [Sounds](../common/sounds.md) | `Sound.adventure` property |
| `vector/VectorUtils.kt` | [Locations and Vectors](../common/locations.md) | `Vector.ZERO` |
| `world/WorldUtils.kt` | [Worlds and Biomes](../common/worlds.md) | nothing — pure delegation |

Because these are top-level declarations, importing is per-function or per-package —
`import me.wyne.wutils.common.kotlin.item.*` — not per-class.

## What is genuinely new here

### `reified` enum reads

The Java `ConfigUtils` enum helpers all take a trailing `Class<E>`. The Kotlin versions
infer it (`config/ConfigUtils.kt:71-84`), so `section.getEnumSet<Material>("blocks")`
replaces `ConfigUtils.getEnumSet(section, "blocks", Material.class)`. `getByName` and
`getByKeyOrName` come in both a free-function and a `ConfigurationSection` form, and both
return `E?` — the nullability is real, not a platform type.

### Kotlin-native ranges

`range/RangeUtils.kt` bridges WUtils's range types to Kotlin's. `ClosedIntRange.range`
gives an `IntRange` and `IntRange.range` gives a `ClosedIntRange`
(`range/RangeUtils.kt:11-15`) — the same property name in both directions, which reads
well in a chain but tells you nothing about which way you are converting. The same
symmetric pair exists for doubles (`range/RangeUtils.kt:17-21`).

`LocationRange.locations(step)` (`range/RangeUtils.kt:29-30`) wraps the Java iterator in
an `Iterable`, which is what makes `for (loc in range.locations())` work.

`DoubleRange.random()` (`range/RangeUtils.kt:31-34`) samples a Kotlin range **inclusively**
of `endInclusive`, and a single-point range yields its value. It does not reimplement the
sampling — it delegates to `Range.randomInclusive` (`Range.java:46-54`), the same
implementation the Java [`DoubleRange`](../common/ranges.md) and `VectorRange` use, so the
two sides cannot drift apart. An empty range — one whose start exceeds its end — throws
`IllegalArgumentException`; `randomOrNull()` (`range/RangeUtils.kt:37-38`) returns `null`
for that case instead.

Be aware that this file declares `typealias DoubleRange = ClosedFloatingPointRange<Double>`
(`range/RangeUtils.kt:9`), which **collides by simple name with WUtils's own
`me.wyne.wutils.common.range.DoubleRange`**. The file has to write its own typealias out
in full to disambiguate against the Java class it imports
(`range/RangeUtils.kt:17-27`). The same collision reaches your code: importing
`me.wyne.wutils.common.kotlin.range.*` alongside the Java range types means `DoubleRange`
resolves to the Kotlin alias, and the two are not interchangeable — that is what the
conversion properties exist to bridge.

Note that `config/ConfigUtils.kt` also has `getIntRange`/`getDoubleRange`
(`config/ConfigUtils.kt:93-107`), and these are **not** the WUtils range types — they
parse `min..max` into Kotlin's `IntRange` and `ClosedFloatingPointRange<Double>`, with a
default when the path is absent. The similarly named `getVectorRange`,
`getLocationRange` and `getTimeSpanRange` on the same receiver do return WUtils types.

### The fuel table

`item/ItemUtils.kt:59-109` is the one substantial piece of data in the module: a
`Map<Material, Int>` of furnace burn times, assembled from explicit entries plus a dozen
Bukkit `Tag` groups (planks, slabs, stairs, fences, wool, carpets, boats, banners...) and
Paper's `MaterialTags.WOODEN_GATES`. It is exposed through `Material.fuelTicks` and
`ItemStack.fuelTicks` (`item/ItemUtils.kt:53-57`), which return `0` for anything not in
the table rather than null.

This is data, not delegation — it has no Java counterpart in `common`, and it encodes
1.16-era vanilla values. Treat it as a snapshot to check against the version you target.

### Small conveniences

`Double.test()` (`doubles/DoubleUtils.kt:5-6`) is `Random.nextDouble() < this` — a
probability roll, so `0.12.test()` is "12% of the time". Short and readable at the call
site, and the whole of the `doubles` package.

`Player.expToLevelUp`, `Player.currentExp` and `OfflinePlayer.exists`
(`player/PlayerUtils.kt:7-22`) turn accessor calls into properties. `Sound.adventure`
(`sound/SoundUtils.kt:6-7`) converts a Bukkit `Sound` to an Adventure one.

`suggest` and `tooltip` (`command/CommandUtils.kt:9-19`) wrap CommandAPI's
`replaceSuggestions` so a suggestion list can be attached inline while keeping the
argument's declared type, which the raw API loses.

## See also

- [WUtils Common](../common/common.md) — the module this wraps; every behavioural
  question is answered there, not here.
- [Ranges](../common/ranges.md) — the WUtils range types and their sampling semantics.
- [Config Utilities](../common/config-utils.md) — the Java side of the config reads.
- [WUtils Internationalization Kotlin](../i18nKt/i18nKt.md) — the sibling Kotlin layer,
  built the same way over `i18n`.
