# Getting Started

WUtils is a collection of Java/Kotlin libraries for **Bukkit/Paper 1.16.5** plugins,
published as separate Maven Central artifacts under the group `io.github.wyne10`. Each
module — events and scheduling, YAML config, internationalization, animations, JDBC
pooling, and so on — is independently versioned and independently useful.

It is **not** a framework and it is **not a plugin**. There is nothing to install on a
server, no bootstrap class to extend, no central "WUtils" you configure. You depend on
just the modules you want, the same way you'd depend on Guava or Gson, and you shade the
classes you use straight into your own plugin jar.

## Which modules exist

| Module | Artifact | Version | Use it for |
|---|---|---|---|
| [Common Toolkit](common/common.md) | `wutils-common` | 1.16.5 | Events, scheduler, promises, terminables, item/location/world helpers, ranges, durations — the shared foundation most other modules build on |
| [Kotlin Support](kotlin/kotlin.md) | `wutils-common-kotlin` | 1.16.5 | Kotlin extension functions over `wutils-common` (reified enum reads, native ranges, DSL-style helpers) |
| [Configuration](config/config.md) | `wutils-config` | 2.10.1 | Annotate a class, generate and read its YAML config automatically |
| [Configurables](configurables/configurables.md) | `wutils-configurables` | 1.21.8 | Ready-made config-driven types for items, GUIs, animations and interactions, built on `wutils-config` |
| [Internationalization](i18n/i18n.md) | `wutils-i18n` | 5.6.1 | Per-player languages, MiniMessage/legacy text, PlaceholderAPI integration |
| [Kotlin Support](kotlin/kotlin.md) | `wutils-i18n-kotlin` | 5.6.1 | Kotlin extensions over `wutils-i18n` — infix replacement builders, accessor shorthands |
| [Animations](animation/animation.md) | `wutils-animation` | 2.2.3 | Sequential/parallel orchestration of particles, sounds, fireworks and titles |
| [Structures](structure/structure.md) | `wutils-structure` | 1.2.7 | Configurable schematic placement via WorldEdit/WorldGuard |
| [Databases](jdbc/jdbc.md) | `wutils-jdbc` | 2.0.0 | A pooled `Connection`/`ConnectionSource` over HikariCP and/or ORMLite, plus runtime JDBC driver download |
| [JSON Storage](json/json.md) | `wutils-json` | 1.2.1 | Annotate a field, write/read it as its own JSON file |

There is **no BOM and no single "WUtils version"**. Modules version independently —
`wutils-jdbc` being on `2.0.0` says nothing about what version `wutils-config` is on. Pin
each dependency's version separately, and check Maven Central (or the badges in the
project [README](https://github.com/Wyne10/WUtils)) rather than assuming they move
together.

## Adding a module

Gradle (Kotlin DSL), the primary form used throughout this wiki:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.wyne10:wutils-common:1.16.5")
}
```

Gradle (Groovy DSL):

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.wyne10:wutils-common:1.16.5'
}
```

Maven:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.wyne10</groupId>
        <artifactId>wutils-common</artifactId>
        <version>1.16.5</version>
    </dependency>
</dependencies>
```

Add as many modules as you need, each with its own version, following the table above.

## Shading

WUtils artifacts are plain libraries, not plugins — nothing loads them unless your own
plugin jar contains their classes. In practice that means running the
[Shadow Gradle plugin](https://gradleup.com/shadow/) (or your Maven equivalent) and
relocating WUtils's packages, so two plugins on the same server that each bundle a
different WUtils version don't collide at runtime:

```kotlin
plugins {
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    implementation("io.github.wyne10:wutils-common:1.16.5")
    implementation("io.github.wyne10:wutils-jdbc:2.0.0")
}

tasks.shadowJar {
    archiveClassifier.set("")
    relocate("me.wyne.wutils", "com.example.myplugin.libs.wutils")
}
```

Build with `./gradlew shadowJar` and deploy the jar `shadowJar` produces (not the plain
`jar` task's output) — that's the one with WUtils's classes actually inside it.

## The `compileOnly` model — read this before you deploy

Every WUtils module compiles against a handful of third-party libraries as
`compileOnly`: WUtils's own published jar does **not** contain their classes, only
references to them. This is normal for a library and it is the single most common way a
reader breaks their plugin, because the failure doesn't show up until runtime.

Two different things fall under "third-party dependency," and they need different
handling:

- **Server- or plugin-provided.** Paper API is always present, because the server *is*
  Paper. WorldEdit, WorldGuard and PlaceholderAPI are separate plugins — if the server
  admin has them installed, their classes are already on the classpath. You still declare
  them `compileOnly` in your own build (so you can compile against them), but you never
  shade them, and a missing one means the corresponding plugin isn't installed, not that
  your build was wrong.
- **Plain libraries nothing supplies for you.** HikariCP, ORMLite, Gson, log4j,
  Adventure/MiniMessage components beyond what Paper's own API already exposes,
  EnhancedLegacyText, triumph-gui, InvUI, CommandAPI, and `kotlin-stdlib` for the Kotlin
  modules. Nothing on a stock server provides these. You add them yourself — usually as
  `implementation`, shaded and relocated the same way as WUtils itself — or the
  corresponding WUtils feature has nothing to load at runtime.

Miss one of the second kind and your build still succeeds — `compileOnly` is enough to
satisfy the compiler. What you get instead is a `NoClassDefFoundError` (or
`ClassNotFoundException`) the first time code that needs the missing class actually runs:

```java
// Compiles fine with no HikariCP anywhere on your classpath — wutils-jdbc only
// needed it at compile time, via compileOnly.
ConnectionPool<HikariDataSource> pool =
        new HikariConnectionPool("jdbc:mysql://localhost/db", "user", "pass");
// java.lang.NoClassDefFoundError: com/zaxxer/hikari/HikariDataSource
// — thrown right here, on first use, if you never added com.zaxxer:HikariCP
// as your own dependency.
```

Each module page under [Which modules exist](#which-modules-exist) lists exactly which
third-party dependencies it needs and for which feature, so check the page for anything
you add before you ship.

## Which module do I want?

| I want to... | Reach for |
|---|---|
| Listen to Bukkit events, schedule tasks, chain async work | [Common Toolkit](common/common.md) — see also [Scheduling and Async Work](common/async.md), [Events](common/events.md) |
| Work with items, locations, worlds, players more conveniently | [Common Toolkit](common/common.md) — see [Items, Players and Worlds](common/game-objects.md) |
| Do any of the above from Kotlin, with reified generics and native ranges | [Kotlin Support](kotlin/kotlin.md) |
| Generate and read a YAML config file from an annotated class | [Configuration](config/config.md) |
| Skip writing your own item/GUI/animation/interaction config types | [Configurables](configurables/configurables.md) |
| Send translated, per-player messages with placeholders | [Internationalization](i18n/i18n.md), [Sending Messages](i18n/messages.md) |
| Play a sequence or burst of particles, sounds, fireworks, titles | [Animations](animation/animation.md) |
| Paste a WorldEdit schematic as a configurable structure | [Structures](structure/structure.md) |
| Get a pooled JDBC connection, optionally downloading the driver at runtime | [Databases](jdbc/jdbc.md) |
| Persist a plain object's field to its own JSON file | [JSON Storage](json/json.md) |

## Platform and license

Every module targets **Java 16** source level and is built against **Paper 1.16.5**
(`com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT`) where it touches Bukkit at
all — `wutils-jdbc` and `wutils-json` don't, and work in any JVM context.

WUtils is licensed **GPL-3.0**. Shading WUtils's classes into your plugin jar makes the
result a combined work, and GPL-3.0 requires that if you distribute that jar, you make
the complete corresponding source available under GPL-3.0 too. That's a stronger
condition than the MIT/Apache-licensed libraries you may be used to shading, and worth
understanding before you ship a closed-source plugin — this isn't legal advice, so read
the [license text](https://www.gnu.org/licenses/gpl-3.0.txt) or talk to a lawyer if you
need certainty.
