# Configuration

`wutils-config` turns annotated fields into a YAML config file: you mark a field with
`@ConfigEntry`, register the class that holds it, and the module generates the file,
merges new keys into whatever the server owner has on disk, and reads values back at
startup or on `/reload`. Reach for it any time a plugin needs a `config.yml` — it is the
module almost every other WUtils module builds on top of, including
[Configurables](../configurables/configurables.md).

Skip it if your config is one or two values you're happy to read with plain
`FileConfiguration#getString` calls — the annotation machinery earns its keep once you
have more than a handful of settings, or once you want typed values (ranges, durations,
custom objects) instead of raw strings.

## Adding it to your build

```kotlin
dependencies {
    implementation("io.github.wyne10:wutils-config:2.10.1")
}
```

`wutils-config` bundles its own copies of `yaml-config-updater` and `javatuples` — you
don't add those yourself, but you do need to shade (or relocate) `wutils-config` itself
into your plugin jar, same as any other WUtils module.

## Third-party dependencies you must supply

| Dependency | Needed for | Scope |
|---|---|---|
| Bukkit/Paper API | everything — `ConfigurationSection`, `YamlConfiguration`, `Plugin` | `compileOnly`, supply at runtime |
| `log4j-core` | quieting `yaml-config-updater`'s own logging at class-load time | `compileOnly`, supply at runtime |

Both are already on a Paper server's classpath, so in practice you rarely have to think
about this list — it matters mainly if you're running `wutils-config` somewhere unusual
(a unit test, a non-Paper harness).

## The full loop: annotate, register, generate, read, reload

One example, carried through every step. Say you want a "welcome message" feature a
server owner can tune without touching your code:

```java
public class WelcomeConfig {

    @ConfigEntry(section = "welcome", comment = "Whether new players get a welcome message")
    public static boolean enabled = true;

    @ConfigEntry(section = "welcome", path = "message")
    public static String message = "Welcome to the server!";

    @ConfigEntry(section = "welcome", path = "delaySeconds", comment = "Seconds to wait before sending the message")
    public static int delaySeconds = 5;
}
```

`section()` is required; `path()` defaults to the field's own name, so `enabled` didn't
need one. `comment()` renders as a `#` line above the field in the generated file.

Wire it up in `onEnable`:

```java
public final class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("config.yml", false); // creates the live file from your bundled default, if missing

        Config.global.setConfigGenerator(this, "config.yml");
        Config.global.registerConfigObject(new WelcomeConfig());
        Config.global.generateConfig();

        Config.global.reloadConfig(getConfig());
    }
}
```

`Config.global` is a shared instance provided for convenience — nothing stops you from
constructing your own `new Config()` and holding onto it instead, e.g. if two of your
plugins must not share registered fields.

`registerConfigObject` walks `object.getClass().getDeclaredFields()` — **only fields
declared directly on that class**, never ones inherited from a superclass. Register an
instance, not the `Class` itself, even though the fields above are `static`.

`setConfigGenerator(Plugin, String)` copies the plugin jar's bundled `config.yml`
resource out to `<dataFolder>/defaults/config.yml` as the starting point for the
generated default file. That resource must actually exist in your jar (put it in
`src/main/resources/config.yml`) — if it doesn't, `Plugin#getResource` returns `null`
and the copy throws an uncaught `NullPointerException` that crashes plugin startup
instead of logging an error.

After `generateConfig()`, calling `Config.global.reloadConfig(getConfig())` reads
whatever is now in the live `config.yml` back into `WelcomeConfig.enabled`,
`WelcomeConfig.message` and `WelcomeConfig.delaySeconds`. From then on, your code just
reads the static fields directly:

```java
if (WelcomeConfig.enabled) {
    Bukkit.getScheduler().runTaskLater(this,
            () -> player.sendMessage(WelcomeConfig.message),
            WelcomeConfig.delaySeconds * 20L);
}
```

Call `reloadConfig` again on `/reload` (or your own reload command) to pick up edits
without restarting the server.

## The two files, and which one you edit

Generation touches **two separate files**:

- The **live config** — `<dataFolder>/config.yml`, or wherever you pointed
  `setConfigGenerator` — is the file your server owner actually edits. This is the only
  file a human should ever touch by hand.
- The **generated default** — `<dataFolder>/defaults/config.yml` — is rebuilt from your
  registered fields on *every* `generateConfig()` call. Never hand-edit it; your changes
  are gone the next time the plugin starts.

`generateConfig()` always rewrites the default file. It only touches the live file
conditionally, driven by a `regenerate: true` flag the live file may or may not contain:

- If the live config does **not** have `regenerate: true`, only the default file is
  rewritten. The live config is left exactly as your server owner last saved it.
- If it **does**, the freshly generated default is merged into the live config (adding
  new keys, leaving the owner's existing values alone), and the `regenerate` key is then
  stripped back out of the live file — it's a one-shot switch, not a persistent setting.

A convenient way to use this: ship a `config.yml` resource in your jar whose entire
content is `regenerate: true`. On first launch, `saveResource` copies that one line into
the live config; `generateConfig()` sees it, merges in every registered field's default
value, and clears the flag. From then on the live file has real content and further
regenerations leave it alone unless an admin (or you, on a version bump) sets
`regenerate: true` again to pull in newly added keys.

**One more trap in that same bundled resource:** every time the default file is rebuilt,
`ConfigGenerator` reads the *existing* default file and unconditionally discards its
first line before appending your registered sections. If your bundled resource's first
line is a comment banner rather than throwaway content, that banner disappears from the
default file on the very next regeneration. Keep the first line disposable (blank, or the
`regenerate: true` line above) if you want it to survive.

## `generateConfig` mutates the list you pass it

`generateConfig(boolean backup, Map<String, String> replaceVars, List<String> deleteProps)`
adds the literal string `"regenerate"` to `deleteProps` **in place**, so that key is
always stripped from the live config after a merge. Pass a mutable list —
`new ArrayList<>()`, not `List.of(...)` — or you get an `UnsupportedOperationException`.
The no-argument `generateConfig()` shown above avoids this entirely; it builds its own
mutable list internally.

## Sections and paths: where a value is actually read from

`section()` supports at most one level of `primary.sub` nesting, and only the `primary`
part (before the first `.`) decides where a field is *read from* — the lookup path is
always `primary.path`. The `sub` part only changes how the field is grouped, under a
`### sub` comment header, in the generated text.

This is easy to get wrong when two fields use the same `path()` under different `sub`
groups of the same primary section:

```java
@ConfigEntry(section = "rewards.items", path = "material", comment = "Item reward material")
public static String itemMaterial = "DIAMOND";

@ConfigEntry(section = "rewards.messages", path = "material", comment = "Placeholder text for the reward message")
public static String messageMaterial = "diamond";
```

Both resolve to the exact same lookup path, `rewards.material` — and because a `sub`
group is only a comment, not real YAML nesting, the generated file ends up with two
sibling `material:` keys directly under `rewards:`:

```yaml
rewards:
  ### items
  # Item reward material
  material: 'DIAMOND'
  ### messages
  # Placeholder text for the reward message
  material: 'diamond'
```

That's a duplicate key in the emitted YAML, and on reload both fields read from whichever
value a parser resolves `rewards.material` to. Give every field under a shared `primary`
section a distinct `path()`, regardless of how you group them with `sub`.

## Sharp edges

- **String coercion on load can hide a typo.** If a field's declared type is `String`,
  reading a YAML `true` or `42` into it succeeds, storing the text `"true"` or `"42"`
  instead of failing — a config typo that should have been caught looks like it worked.
- **A field written to config but never read back is a serialize-only field.** If its
  current value implements the write-only half of the [serialization
  contract](serialization.md) rather than the read-write half, `reloadConfig`/`loadConfig`
  silently skip it. See [Custom Config Types](serialization.md).
- **The field must already hold a non-null value before you reload.** Reloading calls
  `fromConfig` on the field's *current* value for any custom type; a `null` field is
  skipped, not initialized from config. Always give these fields a default in their
  declaration.
- **`loadConfig(section, object)` only reloads fields registered on that exact
  instance** (`==`, not `equals`) — use it to refresh one holder; use `reloadConfig` to
  refresh everything registered.

## See also

- [Custom Config Types](serialization.md) — how a field's value renders itself into the
  YAML this page describes generating, and how to make your own types work here.
- [Configurables](../configurables/configurables.md) — a library of ready-made field
  types (items, GUIs, ranges, durations, ...) built directly on this module.
- [JSON Storage](../json/json.md) — an independent module with a similar shape (annotate
  a field, register the holder) for a different storage format.
