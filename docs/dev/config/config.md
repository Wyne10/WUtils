# WUtils Config

`config` is WUtils' annotation-driven YAML configuration toolkit: mark fields with
`@ConfigEntry`, register the owning object, and the module can generate a default
config file from those fields, merge that default into a live config on disk, and
reload values back into the fields at runtime.

- Directory: `config/`
- Gradle project: `:WUtils-config`
- Maven artifact: `io.github.wyne10:wutils-config`
- Version: `2.10.1`
- Root packages: `me.wyne.wutils.config` (this page), `me.wyne.wutils.config.configurable`
  (see [Serialization](serialization.md))

Source of these facts: `config/build.gradle`.

## Dependencies

| Dependency | Scope | Notes |
|---|---|---|
| `org.bukkit:bukkit:1.15.2-R0.1-SNAPSHOT` | `compileOnly` | Only plain Bukkit types are used (`ConfigurationSection`, `YamlConfiguration`, `Plugin`) — targets Bukkit 1.15.2, not Paper 1.16.5 like most of the repo. Consumer supplies it. |
| `org.apache.logging.log4j:log4j-core:2.26.1` | `compileOnly` | Used only to quiet the bundled `yaml-config-updater`'s logger at class-load time. Consumer supplies it. |
| `ru.vyarus:yaml-config-updater:1.4.4` | `implementation` (bundled) | Does the actual live-config merge; ships inside this module's jar. |
| `org.javatuples:javatuples:1.2` | `implementation` (bundled) | Supplies `Pair`, used internally to carry `(section, ConfigField)` and `(depth, path)` pairs. |

`config` has **no dependency on any other WUtils module**, and targeting plain Bukkit
1.15.2 rather than Paper 1.16.5 makes it the least demanding module in WUtils to
consume.

## Package inventory

### `me.wyne.wutils.config`

| Class | Role |
|---|---|
| `ConfigEntry` | The field annotation that drives everything else. `config/src/main/java/me/wyne/wutils/config/ConfigEntry.java` |
| `Config` | Entry point: registers annotated objects, reloads/loads values, drives generation. `config/src/main/java/me/wyne/wutils/config/Config.java` |
| `ConfigField` | A resolved, reflection-backed `@ConfigEntry` field plus its rendered value. `config/src/main/java/me/wyne/wutils/config/ConfigField.java` |
| `ConfigFieldParser` | Turns annotated fields into `ConfigField`s and groups them into `ConfigSection`s. `config/src/main/java/me/wyne/wutils/config/ConfigFieldParser.java` |
| `ConfigSection` | One top-level YAML section's fields, grouped by sub-section, with YAML rendering. `config/src/main/java/me/wyne/wutils/config/ConfigSection.java` |
| `ConfigGenerator` | Owns the two files on disk and drives the write/merge cycle. `config/src/main/java/me/wyne/wutils/config/ConfigGenerator.java` |
| `PluginUtils` | Package-private internal helper (owning-plugin/logger/version resolution); not public API, and duplicated with minor variations across several WUtils modules. `config/src/main/java/me/wyne/wutils/config/PluginUtils.java` |

### `me.wyne.wutils.config.configurable`

`ConfigSerializable`, `ConfigDeserializable`, `CompositeConfigSerializable`, and
`ConfigBuilder` — see [Serialization](serialization.md).

## The model: annotate, register, generate

1. Annotate a field with `@ConfigEntry` (`config/src/main/java/me/wyne/wutils/config/ConfigEntry.java:17`),
   giving it a `section()` and, optionally, a `path()`, a `comment()`, and `load()`.
2. Call `registerConfigObject(Object)` (`Config.java:90-97`) on the holder instance. It
   walks `object.getClass().getDeclaredFields()` — **only fields declared directly on
   that class**, never inherited ones — and registers every `@ConfigEntry` field it
   finds under its section, via `ConfigFieldParser.getSectionedConfigField`.
3. Call `Config#generateConfig` to write the default config and, if requested, merge it
   into the live config (below).
4. Call `reloadConfig`/`loadConfig` to read values back from a live config into the
   registered fields.

A shared `Config.global` instance (`Config.java:34`) is provided for convenience, but
nothing requires it — constructing and holding a private `Config` is equally valid.
`Config.logger` (`Config.java:35`) is a public, mutable field; a consumer can swap it
for their own logger.

### Sections: one level of nesting, and where the value is actually read from

`ConfigEntry.section()` supports at most `primary.sub` — one level of nesting. Only the
text before the first `.` (lower-cased, spaces stripped) is used to build the field's
**lookup path**: `primarysection.path` (`ConfigFieldParser.java:48-51`). The `sub`
segment, and anything after a second `.`, is ignored for lookup purposes — it only
affects layout in the generated file, grouping the field under a `### sub` comment
header within its primary section (`ConfigFieldParser.java:75-97`, `ConfigSection.java:56-70`).

This is easy to get wrong: two fields in sections `"rewards.items"` and
`"rewards.messages"` are read from the same top-level YAML key, `rewards`, no matter
what their `sub` differs to — the sub-section is purely cosmetic grouping in the
generated text.

## The two files: live config vs. generated default

Generation involves **two separate files**, and the module's confusing part is exactly
how they interact:

- The **live config** — the file the plugin actually reads at runtime, at whatever path
  the consumer points `ConfigGenerator` at (typically `<dataFolder>/<configPath>`).
- The **generated default** — a second file, conventionally at
  `<dataFolder>/defaults/<configPath>`, that this module rebuilds from the registered
  fields every time generation runs.

Call `Config#setConfigGenerator` first to point at both. The `(Plugin, String)`
overload (`Config.java:69-81`) copies the plugin's bundled resource at `configPath` out
to `<dataFolder>/defaults/<configPath>` as the starting default file — but if
`configPath` does not name a resource actually bundled in the plugin's jar,
`Plugin#getResource` returns `null`, which is passed straight into `Files.copy`. The
resulting `NullPointerException` is **not caught** and propagates out of
`setConfigGenerator`, crashing plugin startup rather than logging an error. A typo'd
resource path is a hard crash, not a warning.

`Config#generateConfig(boolean, Map, List)` (`Config.java:180-190`) then runs, in order:

1. Adds the literal string `"regenerate"` to the caller's `deleteProps` list —
   **mutating that list in place**; passing an immutable list throws
   `UnsupportedOperationException`.
2. `ConfigGenerator.copyDefaultConfig()` (`ConfigGenerator.java:49-58`) reads the
   *existing* default file into the in-progress generated text, **skipping its first
   line** unconditionally. Anything hand-written or previously generated on line 1 of the
   default file is discarded on every regeneration.
3. `ConfigGenerator.writeConfigSections(...)` (`ConfigGenerator.java:38-42`) appends
   every registered section's rendered YAML after that.
4. `ConfigGenerator.generateConfig(boolean, Map, List)` (`ConfigGenerator.java:68-88`)
   **unconditionally overwrites the default config file** with the full accumulated
   text — this always happens, regardless of the live config's state.

Only the last step touches the live config, and only conditionally: the live config on
disk is read for a `regenerate: true/false` flag; if it's `false`, only the default
file gets rewritten and the live config is left untouched; if it's `true`, the default
file is rewritten *and* merged into the live config, which also strips the
now-consumed `regenerate` key back out.

`YamlUpdater.create(configFile, defaultConfigFile)...update()` only runs when the
**live** config currently contains `regenerate: true`. Since `"regenerate"` was added
to `deleteProps` in step 1, the merge always strips that key back out of the live
config afterward — it is a one-shot trigger a user (or an admin editing the file by
hand) sets to request the next regeneration, and it clears itself once consumed.

Net effect: the default file is rebuilt from the registered fields on every call, but
the live config is only ever touched when someone has explicitly asked for it.

## Reading values back: `reloadConfig` and `loadConfig`

Both methods (`Config.java:117-139`, `146-169`) walk every registered field with
`ConfigEntry.load() == true` whose lookup path exists in the given
`ConfigurationSection`, and for each:

- If the field's **current value** implements `ConfigDeserializable`, calls
  `fromConfig(...)` on it in place (the field itself is left pointing at the same
  object).
- Else if the current value implements plain `ConfigSerializable` (serialize-only, no
  `fromConfig`), it is **skipped** — such a field is written to config but never read
  back.
- Otherwise the field is set directly from the config value, via reflection. If the
  field's declared type is `String`, the value is coerced with `String.valueOf(...)` —
  so a YAML number or boolean loads as its text form rather than failing.

The only difference between the two methods: `loadConfig(ConfigurationSection, Object)`
additionally filters to fields whose registered holder **is** `object` (`==`, not
`equals`) — a field registered on some other, even `equals`-equal, instance is left
untouched. Use `loadConfig` to reload just one holder; `reloadConfig` reloads
everything registered.

## Sharp edges

- **`registerConfigObject` only sees declared fields** — `getDeclaredFields()`, so
  fields inherited from a superclass are never registered.
- **Serialize-only fields are a one-way street** — see "Reading values back" above.
- **String coercion on load** can mask a config typo: a YAML `true` or `42` typed into
  a `String` field loads successfully as the text `"true"`/`"42"` instead of failing.
- **`setConfigGenerator(Plugin, String)` throws uncaught `NullPointerException`** for a
  `configPath` that isn't a bundled resource — see above.
- **`generateConfig(boolean, Map, List)` mutates the caller's `deleteProps` list.** Pass
  a mutable list, and expect `"regenerate"` to appear in it afterward.

## See also

- [Serialization](serialization.md) — the `configurable` package: how a field's value
  renders itself into the YAML this page describes generating.
- [WUtils Json](../json/json.md) — an independent, differently-structured module with
  the same shape (annotate a field, register the holder, let a registry read/write it).
- The `configurables` module builds on this one directly (an `api` dependency on
  `:WUtils-config`) with ready-made `ConfigSerializable`/`ConfigDeserializable`/
  `CompositeConfigSerializable` implementations for items, GUIs, and more. It will get
  its own wiki pages later.
