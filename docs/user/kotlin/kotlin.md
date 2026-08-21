# Kotlin Support

`wutils-common-kotlin` and `wutils-i18n-kotlin` are thin Kotlin layers over
[`wutils-common`](../common/common.md) and [`wutils-i18n`](../i18n/i18n.md). Reach for
them if your plugin is written in Kotlin — they turn the Java static-helper style
(`ItemUtils.isNullOrAir(stack)`) into extension functions and properties
(`stack.isNullOrAir()`), and add things that only make sense in Kotlin at all: reified
generics, native `IntRange`/`ClosedFloatingPointRange` interop, and infix builders.

They **add to their Java counterparts, they don't replace them.** Depending on
`wutils-common-kotlin` pulls in `wutils-common` too (see below), and most of what these
modules do is call straight through to the Java API. If your plugin is Java-only, skip
both — there's nothing here for you.

## Adding them

```kotlin
dependencies {
    implementation("io.github.wyne10:wutils-common-kotlin:1.16.5")
    implementation("io.github.wyne10:wutils-i18n-kotlin:5.6.1")
}
```

Both re-export their Java module as an `api` dependency, so pulling in either Kotlin
artifact gives you the corresponding Java one automatically — no separate
`wutils-common`/`wutils-i18n` line needed. Each Kotlin module's version tracks its Java
counterpart exactly (`wutils-common-kotlin` always matches `wutils-common`,
`wutils-i18n-kotlin` always matches `wutils-i18n`), so the two publish in lockstep.

You need `kotlin-stdlib` on your runtime classpath — WUtils declares it `compileOnly`,
so it isn't bundled:

```kotlin
dependencies {
    implementation(kotlin("stdlib"))
}
```

`wutils-i18n-kotlin` additionally needs whatever `wutils-i18n` itself needs for the
features you use — Adventure/MiniMessage for the serializer properties, in particular.
See [Internationalization](../i18n/i18n.md) for the full list. `wutils-common-kotlin`
needs CommandAPI only if you use the `suggest`/`tooltip` extensions on command
arguments.

## Reified enum reads

The Java `ConfigUtils` enum helpers all take a trailing `Class<E>` argument, because Java
erases generics. Kotlin's `reified` type parameters let the Kotlin versions infer it:

```kotlin
import me.wyne.wutils.common.kotlin.config.getEnumSet

val blocks: EnumSet<Material> = section.getEnumSet<Material>("blocks")
```

instead of the Java `ConfigUtils.getEnumSet(section, "blocks", Material.class)`.
`getByName` and `getByKeyOrName` come in both a free-function and a
`ConfigurationSection` receiver form, and both return `E?` — a real nullable type, not a
Java platform type you have to trust:

```kotlin
val effect: PotionEffectType? = getByKeyOrName("SPEED")
val chosen: Material? = section.getByKeyOrName("target-block")
```

## Kotlin-native ranges

WUtils's own range types (`ClosedIntRange`, `DoubleRange`, `LocationRange`, ...) convert
to and from Kotlin's built-in ranges through a `range` property, in both directions:

```kotlin
import me.wyne.wutils.common.kotlin.range.range

val kotlinRange: IntRange = closedIntRange.range   // WUtils -> Kotlin
val wutilsRange: ClosedIntRange = kotlinRange.range // Kotlin -> WUtils
```

The same property name works both ways, which reads well chained but doesn't tell you
which direction you're converting on its own — check the static type at the call site if
it isn't obvious.

`LocationRange.locations(step)` wraps the Java iterator so it works in a `for` loop:

```kotlin
for (loc in locationRange.locations(0.5)) {
    world.spawnParticle(Particle.FLAME, loc, 1)
}
```

A Kotlin `ClosedFloatingPointRange<Double>` gets `random()` and `randomOrNull()`, backed
by the same sampling `wutils-common`'s Java `DoubleRange` uses — `random()` throws
`IllegalArgumentException` on an empty range (start past end), `randomOrNull()` returns
`null` instead:

```kotlin
val roll: Double = (1.0..10.0).random()
val safeRoll: Double? = someRange.randomOrNull()
```

**Watch the name collision.** `commonKt` declares its own `typealias DoubleRange =
ClosedFloatingPointRange<Double>`, which shares a simple name with WUtils's own
`me.wyne.wutils.common.range.DoubleRange`. If you import both
`me.wyne.wutils.common.kotlin.range.*` and the Java range package, `DoubleRange` resolves
to the Kotlin typealias, and the two types aren't interchangeable — that's exactly what
the `range` conversion property exists to bridge.

`ConfigUtils.kt` also has `getIntRange`/`getDoubleRange` on `ConfigurationSection` — these
parse a `min..max` string straight into a plain Kotlin `IntRange`/
`ClosedFloatingPointRange<Double>` with a default, and are **not** the WUtils range types.
`getVectorRange`, `getLocationRange` and `getTimeSpanRange` on the same receiver do
return WUtils types, so don't assume the whole family behaves the same way.

## Small conveniences worth knowing about

```kotlin
import me.wyne.wutils.common.kotlin.doubles.test

if (0.12.test()) {
    // fires ~12% of the time
}
```

```kotlin
import me.wyne.wutils.common.kotlin.player.expToLevelUp
import me.wyne.wutils.common.kotlin.player.currentExp
import me.wyne.wutils.common.kotlin.player.exists

val needed = player.expToLevelUp
val had = player.currentExp
if (!offlinePlayer.exists) return
```

```kotlin
import me.wyne.wutils.common.kotlin.item.fuelTicks

val burnTime: Short = Material.OAK_PLANKS.fuelTicks
```

`Material.fuelTicks`/`ItemStack.fuelTicks` come from a hand-built furnace burn-time table
covering explicit entries plus Bukkit's material `Tag` groups (planks, slabs, stairs,
fences, wool, boats...) and encode 1.16-era vanilla values. It returns `0` for anything
not in the table rather than `null` — treat it as a snapshot to double-check if you're
targeting a different game version.

```kotlin
import me.wyne.wutils.common.kotlin.command.suggest

StringArgument("target")
    .suggest("nearest", "random", "self")
```

`suggest`/`tooltip` wrap CommandAPI's `replaceSuggestions` so a suggestion list attaches
inline while the argument keeps its declared type — the raw CommandAPI call loses it.
Requires CommandAPI on your classpath.

## Idiomatic i18n access

`wutils-i18n-kotlin` collapses the Java accessor chain
(`I18n.global.accessor(player, path).getPlaceholderComponent(player, ...)`) into a
extension call on the audience itself. Four receivers — `Player`, `OfflinePlayer`,
`CommandSender`, and `String` (for the no-audience, default-language case) — each get the
same eight functions:

```kotlin
import me.wyne.wutils.i18n.kotlin.*

player.placeholderComponent("greeting", "player" replace player.name)
      .sendMessage(player)

"messages.motd".localizedStrings("server" replace "MyServer")
```

`placeholder*` variants additionally run PlaceholderAPI for the receiver;
`localized*` variants don't. `OfflinePlayer` and a non-`Player` `CommandSender` always
resolve to the **default language** — only an online `Player` gets their own per-player
locale — and every one of these functions dereferences `I18n.global` with `!!`, so
calling any of them before you've assigned `I18n.global` throws `NullPointerException`.

### Infix replacement builders

`Replacements.kt` turns the `Placeholder`/`ComponentPlaceholder` factories into infix
functions, so a replacement reads as `key infix value`. The receiver is the
**placeholder key**, not the text being substituted in:

```kotlin
val greeting = player.placeholderComponent(
    "greeting",
    "player" replace player.name,
    "count" replace 3
)

val fancyName = "player" replaceComponent Component.text(player.name).color(NamedTextColor.AQUA)
player.placeholderComponent("greeting").replace(fancyName)

val both = ("player" replace player.name) andThen ("count" replace 3)
player.placeholderComponent("greeting", both)
```

Which infix to reach for is the same decision as in Java, and it still matters: `replace`
(and friends ending without `Component`) build a `TextReplacement`, substituted *before*
the interpreter parses the string — its value can inject markup. `replaceComponent`/
`regexComponent` build a `ComponentReplacement`, applied *after* parsing, and can't. See
[the comparison in the Internationalization page](../i18n/i18n.md) if you're not sure
which you need.

### Serializer properties

The static `I18n.serializeX`/`deserializeX` helpers become symmetric extension
properties — the same name serializes on a `Component` and deserializes on a `String`:

```kotlin
val text: String = component.miniMessage
val back: Component = "<red>hi".miniMessage

val plain: String = component.plainText
```

`legacy`, `legacySection`, `gson`, `plain`, `plainText`, `miniMessage` all work this way.
`Component` additionally has `bungee` (to a `BaseComponent[]`), with `component` going
back the other way from `Array<BaseComponent>`.

### Collection helpers

```kotlin
import me.wyne.wutils.i18n.kotlin.reduce

val joined: Component? = listOfLocalizedComponents.reduce()
```

`reduce()`/`reduceRaw()` join a collection with newlines and return `null` for an empty
collection — unlike the Java `I18n.reduce*` helpers, which return an empty
string/`Component` instead. Don't assume the two behave the same way on an empty list.

## See also

- [Common Toolkit](../common/common.md) — the Java module `wutils-common-kotlin` wraps;
  every behavioral question about what a helper actually does is answered there.
- [Internationalization](../i18n/i18n.md) and [Sending Messages](../i18n/messages.md) —
  the Java module and API `wutils-i18n-kotlin` wraps.
- [Getting Started](../getting-started.md) — the `compileOnly` model that governs
  `kotlin-stdlib` and every other dependency mentioned above.
