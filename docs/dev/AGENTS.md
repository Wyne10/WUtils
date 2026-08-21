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
- [WUtils Animation](animation/animation.md)
- [Runnables](animation/runnables.md)
- [Steps and scheduling](animation/steps.md)
- [WUtils Common](common/common.md)
- [Anvil](common/anvil.md)
- [Blocks](common/blocks.md)
- [Commands](common/commands.md)
- [Config Utilities](common/config-utils.md)
- [Durations and Cooldowns](common/durations.md)
- [Events](common/events.md)
- [Gson Serializers](common/gson-serializers.md)
- [Vendored helper Library](common/helper.md)
- [Inventories](common/inventories.md)
- [Items](common/items.md)
- [Loadables](common/loadables.md)
- [Locations and Vectors](common/locations.md)
- [Comparators and Operations](common/operations.md)
- [Particle Data Parsers](common/particles.md)
- [Players](common/players.md)
- [Plugin Composition](common/plugin.md)
- [Ranges](common/ranges.md)
- [Scheduler](common/scheduler.md)
- [Sounds, Randomness and Placeholders](common/sounds.md)
- [Core Utilities](common/utilities.md)
- [Worlds and Biomes](common/worlds.md)
- [WUtils Common Kotlin](commonKt/commonKt.md)
- [WUtils Config](config/config.md)
- [Serialization](config/serialization.md)
- [WUtils Configurables](configurables/configurables.md)
- [Animations](configurables/animations.md)
- [Attributes and Containers](configurables/attributes.md)
- [GUIs](configurables/guis.md)
- [Interactions](configurables/interactions.md)
- [Items](configurables/items.md)
- [Value Configurables](configurables/values.md)
- [WUtils Internationalization](i18n/i18n.md)
- [Interpreters and Validation](i18n/interpreters.md)
- [Languages](i18n/languages.md)
- [Localized Values and Access](i18n/localized.md)
- [Replacements](i18n/replacements.md)
- [WUtils Internationalization Kotlin](i18nKt/i18nKt.md)
- [WUtils JDBC](jdbc/jdbc.md)
- [Driver loading](jdbc/driver-loading.md)
- [WUtils Json](json/json.md)
- [WUtils Structure](structure/structure.md)
- [Terrain Edit Modifiers](structure/edit-modifiers.md)
- [Locations and Conditions](structure/locations.md)
- [Modifiers](structure/modifiers.md)
- [Persistence](structure/persistence.md)
- [Regions and Flags](structure/regions.md)
- [Schemes and Clipboards](structure/schemes.md)
