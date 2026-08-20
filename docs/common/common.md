# WUtils Common

`wutils-common` is the shared toolkit every other WUtils module and consumer plugin
builds on. It is not a framework — it is a pile of independent utilities plus one
vendored concurrency stack, and you take only the parts you need.

- **Artifact**: `io.github.wyne10:wutils-common`, version `1.16.5` (`common/build.gradle:22`)
- **Root package**: `me.wyne.wutils.common`
- **Depends on**: nothing else in WUtils. [`configurables`](../config/config.md) and
  `structure` depend on it.

## Dependencies

| Dependency | Scope | Notes |
|---|---|---|
| `com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT` | `compileOnly` | Used by almost every package. Consumer supplies it. |
| `dev.jorel:commandapi-bukkit-core:9.4.2` | `compileOnly` | Only `CommandUtils` — see [Commands](commands.md). |
| `org.apache.logging.log4j:log4j-core:2.26.1` | `compileOnly` | Only `LoggerWrapper`'s level type — see [Plugin Composition](plugin.md). |
| `org.apache.commons:commons-lang3:3.20.0` | `implementation` (bundled) | Duration formatting in `CooldownMap`/`Period`, stack-trace formatting in `EventRegistry`. |

Both optional `compileOnly` dependencies are scoped to a single class, so a consumer
who touches neither `CommandUtils` nor `LoggerWrapper` needs neither jar. Forgetting
one is a runtime `NoClassDefFoundError` at first use, not a compile error.

Several libraries are used directly but not declared, arriving transitively through
paper-api: Guava (the vendored helper code, and `PluginUtils`), Gson (the four
[Gson Serializers](gson-serializers.md)), Adventure (`SoundUtils`, `SpawnerLoader`),
SLF4J and SnakeYAML. They are available in any Paper runtime, but nothing in this
module's build file pins their versions.

Note that `PAPIUtils` does **not** require PlaceholderAPI — it only concatenates a
placeholder string, and PlaceholderAPI is not a declared dependency of this module at
all.

`common` has **no dependency on any other WUtils module**. A Kotlin extension layer,
`wutils-common-kotlin`, is published alongside it and is finalized by this module's
`publish` task (`common/build.gradle:18-20`).

## The map

**Bukkit helpers** — thin wrappers over the server API, the most commonly reached-for
part of the module:

| Page | Covers |
|---|---|
| [Blocks](blocks.md) | `BlockUtils`, `NaturalBlockBreakEvent` |
| [Items](items.md) | `ItemUtils`, tile-state and spawner persistence |
| [Inventories](inventories.md) | `InventoryUtils` |
| [Players](players.md) | `PlayerUtils` |
| [Worlds and Biomes](worlds.md) | `WorldUtils`, `BiomePreset` |
| [Locations and Vectors](locations.md) | `LocationUtils`, `VectorUtils` |
| [Anvil](anvil.md) | `AnvilUtils` |
| [Commands](commands.md) | `CommandUtils` (CommandAPI) |
| [Sounds, Randomness and Placeholders](sounds.md) | `SoundUtils`, `RandomUtils`, `PAPIUtils` |

**Values from config** — turning configuration text into reusable objects:

| Page | Covers |
|---|---|
| [Config Utilities](config-utils.md) | `ConfigUtils` typed reads |
| [Ranges](ranges.md) | `ClosedIntRange`, `DoubleRange`, `VectorRange`, `LocationRange`, `TimeSpanRange` and their iterators |
| [Durations and Cooldowns](durations.md) | the two `Ticks` classes, the duration units, `TimeSpan`, `CooldownMap`, `Period` |
| [Comparators and Operations](operations.md) | rules like `>= 5` and `*2` |
| [Particle Data Parsers](particles.md) | string → particle data |
| [Gson Serializers](gson-serializers.md) | Base64 items/inventories, cooldown persistence |
| [Loadables](loadables.md) | ordered config loading |
| [Core Utilities](utilities.md) | `Args`, `MapUtils` |

**Lifecycle and concurrency:**

| Page | Covers |
|---|---|
| [Plugin Composition](plugin.md) | `CompositeJavaPlugin`, the `Step`/`StepScope` model, `PluginUtils`, `LoggerWrapper` |
| [Events](events.md) | `EventRegistry`, `ListenerRegistry` |
| [Scheduler](scheduler.md) | the `Scheduler` interface, `PromisedTask`, `ObservableTask`, `EventPromisedTask` |
| [Vendored helper Library](helper.md) | `Promise`, `Terminable`, `Schedulers`, `Events` — vendored from lucko's helper |

## A third of this module is not ours

47 of 153 source files are vendored verbatim-ish from lucko's
[`helper`](https://github.com/lucko/helper) (MIT), covering promises, terminables,
functional event subscription and the scheduler core. They are identified by a
`This file is part of helper, licensed under the MIT License.` header.

Do not edit them, and do not expect this wiki to document their API — read
[Vendored helper Library](helper.md) for the boundary and the
[upstream wiki](https://github.com/lucko/helper/wiki) for the API itself.

Two packages are **mixed**, which is the thing to be careful about: `event/` is 20
vendored files plus 4 first-party, and `scheduler/` is 10 plus 4. Check the header
before touching anything in either.

The vendored copy is also not identical to upstream — it was repackaged, and its
hosting-plugin lookup was rewired to `PluginUtils`. That single static plugin reference
is what every scheduled task, event subscription and promise continuation resolves
through, so its behaviour is worth understanding before deploying WUtils shared between
plugins rather than shaded. [helper.md](helper.md) covers it.

## See also

- [WUtils Config](../config/config.md), [WUtils Internationalization](../i18n/i18n.md),
  [WUtils Animation](../animation/animation.md) — sibling modules with no dependency on
  this one.
