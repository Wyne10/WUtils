# WUtils Contributor Documentation

WUtils is a multi-module collection of independently versioned and
independently published Java/Kotlin libraries for Bukkit/Paper 1.16.5
plugins; each module is its own Maven Central artifact and consumers pull
only the modules they need.

This is the **contributor wiki**: it documents WUtils module by module, package by
package, at the level of detail someone changing the code needs — internal structure,
invariants, sharp edges, and source citations. It covers the `animation`, `common`,
`commonKt`, `config`, `configurables`, `i18n`, `i18nKt`, `jdbc`, `json` and `structure`
modules. The deprecated `log` module is excluded from the build and is not documented.

If you want to *use* WUtils in a plugin rather than work on it, read the
[user wiki](../user/AGENTS.md) instead.
- [WUtils Animation](docs/dev/animation/animation.md)
- [Runnables](docs/dev/animation/runnables.md)
- [Steps and scheduling](docs/dev/animation/steps.md)
- [WUtils Common](docs/dev/common/common.md)
- [Anvil](docs/dev/common/anvil.md)
- [Blocks](docs/dev/common/blocks.md)
- [Commands](docs/dev/common/commands.md)
- [Config Utilities](docs/dev/common/config-utils.md)
- [Durations and Cooldowns](docs/dev/common/durations.md)
- [Events](docs/dev/common/events.md)
- [Gson Serializers](docs/dev/common/gson-serializers.md)
- [Vendored helper Library](docs/dev/common/helper.md)
- [Inventories](docs/dev/common/inventories.md)
- [Items](docs/dev/common/items.md)
- [Loadables](docs/dev/common/loadables.md)
- [Locations and Vectors](docs/dev/common/locations.md)
- [Comparators and Operations](docs/dev/common/operations.md)
- [Particle Data Parsers](docs/dev/common/particles.md)
- [Players](docs/dev/common/players.md)
- [Plugin Composition](docs/dev/common/plugin.md)
- [Ranges](docs/dev/common/ranges.md)
- [Scheduler](docs/dev/common/scheduler.md)
- [Sounds, Randomness and Placeholders](docs/dev/common/sounds.md)
- [Core Utilities](docs/dev/common/utilities.md)
- [Worlds and Biomes](docs/dev/common/worlds.md)
- [WUtils Common Kotlin](docs/dev/commonKt/commonKt.md)
- [WUtils Config](docs/dev/config/config.md)
- [Serialization](docs/dev/config/serialization.md)
- [WUtils Configurables](docs/dev/configurables/configurables.md)
- [Animations](docs/dev/configurables/animations.md)
- [Attributes and Containers](docs/dev/configurables/attributes.md)
- [GUIs](docs/dev/configurables/guis.md)
- [Interactions](docs/dev/configurables/interactions.md)
- [Items](docs/dev/configurables/items.md)
- [Value Configurables](docs/dev/configurables/values.md)
- [WUtils Internationalization](docs/dev/i18n/i18n.md)
- [Interpreters and Validation](docs/dev/i18n/interpreters.md)
- [Languages](docs/dev/i18n/languages.md)
- [Localized Values and Access](docs/dev/i18n/localized.md)
- [Replacements](docs/dev/i18n/replacements.md)
- [WUtils Internationalization Kotlin](docs/dev/i18nKt/i18nKt.md)
- [WUtils JDBC](docs/dev/jdbc/jdbc.md)
- [Driver loading](docs/dev/jdbc/driver-loading.md)
- [WUtils Json](docs/dev/json/json.md)
- [WUtils Structure](docs/dev/structure/structure.md)
- [Terrain Edit Modifiers](docs/dev/structure/edit-modifiers.md)
- [Locations and Conditions](docs/dev/structure/locations.md)
- [Modifiers](docs/dev/structure/modifiers.md)
- [Persistence](docs/dev/structure/persistence.md)
- [Regions and Flags](docs/dev/structure/regions.md)
- [Schemes and Clipboards](docs/dev/structure/schemes.md)
