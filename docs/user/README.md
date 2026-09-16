---
description: >-
  Independently published Java/Kotlin libraries for Bukkit/Paper 1.16.5 plugins.
  Depend on the parts you need — there is nothing to install on a server.
---

# WUtils

WUtils is a collection of Java/Kotlin libraries for **Bukkit/Paper 1.16.5** plugins,
published as separate Maven Central artifacts under the group `io.github.wyne10`.

It is **not** a framework and **not** a plugin. There is nothing to install on a server,
no bootstrap class to extend, no central "WUtils" to configure. You depend on just the
modules you want — the same way you'd depend on Guava or Gson — and shade the classes you
use into your own plugin jar.

Each module is versioned independently. There is no BOM and no single "WUtils version":
`wutils-jdbc` being on `2.0.0` says nothing about what version `wutils-config` is on.

{% hint style="info" %}
New here? [Getting Started](getting-started.md) covers dependency coordinates, the module
table, and shading — read it before anything else.
{% endhint %}

## The modules

| Module | Artifact | Use it for |
|---|---|---|
| [Common Toolkit](common/common.md) | `wutils-common` | Events, scheduler, promises, terminables, item/location/world helpers, ranges, durations — the foundation most other modules build on |
| [Kotlin Support](kotlin/kotlin.md) | `wutils-common-kotlin` | Kotlin extensions over `wutils-common` |
| [Configuration](config/config.md) | `wutils-config` | Annotate a class, generate and read its YAML config automatically |
| [Configurables](configurables/configurables.md) | `wutils-configurables` | Config-driven types for items, GUIs, animations and interactions |
| [Internationalization](i18n/i18n.md) | `wutils-i18n` | Per-player languages, MiniMessage/legacy text, PlaceholderAPI |
| [Animations](animation/animation.md) | `wutils-animation` | Sequential/parallel particles, sounds, fireworks and titles |
| [Structures](structure/structure.md) | `wutils-structure` | Configurable schematic placement via WorldEdit/WorldGuard |
| [Databases](jdbc/jdbc.md) | `wutils-jdbc` | Pooled connections over HikariCP/ORMLite, runtime driver download |
| [JSON Storage](json/json.md) | `wutils-json` | Annotate a field, write and read it as its own JSON file |

## Where to go next

* **Building something config-driven** — [Configuration](config/config.md), then
  [Configurables](configurables/configurables.md).
* **Sending text to players** — [Internationalization](i18n/i18n.md) and
  [Sending Messages](i18n/messages.md).
* **Wiring up a plugin** — [Plugin Setup](common/plugin.md) and
  [Scheduling and Async Work](common/async.md).
* **Changing WUtils itself** — the [Contributing](/contributing) section documents every
  module's internals, invariants and sharp edges.
