# Common Toolkit

`wutils-common` is the foundation module. Everything else in WUtils either depends on it
(`configurables`, `structure`) or duplicates nothing it already provides. It is not a
framework — you don't extend a base plugin class to use it (though you can, see
[Plugin Setup](plugin.md)) — it's a pile of independent Bukkit/Paper helpers plus one
vendored concurrency stack, and you take only the parts you need.

Reach for it when you want: durability/drop-accurate block breaking, a scheduler that
returns chainable promises instead of raw Bukkit task IDs, config-string parsers for
ranges/durations/comparators, or a declarative plugin bootstrap. Skip it if you only need
one or two of these — nothing stops you from depending on `wutils-common` just for
`ItemUtils`, since there's no shared state to initialize first.

## Adding it to your build

```kotlin
dependencies {
    implementation("io.github.wyne10:wutils-common:1.16.5")
}
```

Shade it into your plugin jar (or relocate it) unless you have a specific reason to load
it once and share it across plugins — see [the plugin-lookup warning](async.md#which-plugin-a-task-belongs-to)
before you consider sharing.

## Third-party dependencies you must supply

| Dependency | Needed for | Scope |
|---|---|---|
| Paper API 1.16.5 | almost everything | `compileOnly` — you supply it, normally already on your classpath as a plugin |
| [CommandAPI](https://commandapi.jorel.dev/) 9.4.2+ | `CommandUtils` only, see [Plugin Setup](plugin.md#commands) | `compileOnly` |
| log4j-core 2.26.1+ | `LoggerWrapper`'s level type only, see [Plugin Setup](plugin.md#loggerwrapper) | `compileOnly` |
| PlaceholderAPI | only meaningful if you register a PAPI expansion; `PAPIUtils` itself doesn't require it | not declared at all |

Guava, Gson, Adventure, SLF4J and SnakeYAML are used internally but arrive transitively
through Paper's own classpath at runtime — you don't need to add them yourself on a real
Paper server, just don't rely on their versions being pinned by this module.

Touching `CommandUtils` without CommandAPI on the classpath, or `LoggerWrapper`'s level
type without log4j, fails at class-load time with `NoClassDefFoundError` — not at compile
time, because both are `compileOnly`.

## What's in here

| Page | Covers |
|---|---|
| [Scheduling and Async Work](async.md) | running things off the main thread and back, promises, repeating tasks |
| [Events](events.md) | `EventRegistry` and `ListenerRegistry`, plus the vendored functional event API |
| [Items, Players and Worlds](game-objects.md) | items, inventories, players, blocks, worlds/biomes, locations/vectors, the anvil, sounds, particles |
| [Plugin Setup](plugin.md) | bootstrapping your plugin's main class, ordered startup, config loading order |
| [Ranges, Durations and Values](values.md) | parsing config strings into ranges, durations, comparators, operations, and other small utilities |

## Half of this is vendored, and it matters which half

Roughly a third of this module's source is vendored, near-verbatim, from lucko's
[`helper`](https://github.com/lucko/helper) library (MIT license). It supplies the
promise/scheduler/event/terminable machinery everything else in the module (and
`structure`, `configurables`) is built on:

- `Promise` — a future-like type that's aware of the Bukkit main thread, so a chain of
  callbacks can hop between sync and async explicitly.
- `Schedulers` / `Task` — the underlying task scheduling this module's own `Scheduler`
  interface builds on.
- `Events` / `Subscription` — fluent, filtered, self-expiring event subscriptions,
  distinct from this module's own `EventRegistry`/`ListenerRegistry`.
- `Terminable` / `CompositeTerminable` — an `AutoCloseable` refinement plus containers
  that close a group of registered things together, which is how subscriptions and tasks
  get torn down when your plugin disables.

**This wiki does not re-document that API.** For the full picture read the
[upstream helper wiki](https://github.com/lucko/helper/wiki). What you do need to know,
because it changes how you call these classes safely, is covered in
[Scheduling and Async Work](async.md#which-plugin-a-task-belongs-to): the vendored code
was repackaged under `me.wyne.wutils.common`, and every scheduled task, subscription and
promise continuation resolves its owning plugin through a single static reference
(`PluginUtils`). That's usually invisible — but it's the thing to understand before you
ever load this module outside of a normal single-plugin shaded jar.

The rest of the module — everything covered on the other five pages — is first-party
WUtils code with no such caveat.

## Nullability

Unless a parameter or return type is explicitly marked `@Nullable` in your IDE, treat it
as non-null — that's a deliberate, actively-maintained contract on this module's own
code (not the vendored parts, which use a different annotation and aren't part of this
guarantee). If your IDE shows you a platform type from Kotlin instead of a clear
`String?`/`String`, you're probably looking at a vendored class.

## See also

- [WUtils Config](../config/config.md), [WUtils Internationalization](../i18n/i18n.md),
  [WUtils Animation](../animation/animation.md) — sibling modules with no dependency on
  this one.
- [Kotlin Support](../kotlin/kotlin.md) — `wutils-common-kotlin`, extensions over this
  module for Kotlin consumers.
- [contributor wiki: WUtils Common](../../dev/common/common.md) — internals, source
  locations, sharp edges in more depth.
