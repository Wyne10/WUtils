# Custom Config Types

[Configuration](config.md) covers plain fields — `String`, `int`, `boolean`. This page
is for when you have a type `wutils-config` doesn't already know how to render or parse:
implement one or two small interfaces from `me.wyne.wutils.config.configurable`, and your
type works as a `@ConfigEntry` field exactly like a built-in one.

You don't need a separate Gradle dependency for this — it's the same `wutils-config`
artifact as [Configuration](config.md).

## The contract

| Interface | Adds | Use it when |
|---|---|---|
| `ConfigSerializable` | `toConfig(ConfigEntry)` — render to YAML text | your type is built by a factory and never mutated in place |
| `ConfigDeserializable` | extends the above, adds `fromConfig(Object)` — repopulate this instance in place | you want the field re-read on every reload (the common case) |
| `CompositeConfigSerializable` | extends `ConfigSerializable`, adds a depth-aware `toConfig(int depth, ConfigEntry)` | your type renders as more than one line and needs to nest correctly |

A field whose current value implements plain `ConfigSerializable` but not
`ConfigDeserializable` is written to the generated file but silently skipped on reload —
that's a deliberate one-way street for values you rebuild from a factory rather than
mutate. See [Configuration's reload section](config.md#the-full-loop-annotate-register-generate-read-reload).
For anything you want to `/reload`, implement `ConfigDeserializable`.

If your type renders as a nested block rather than a single scalar, implement
`CompositeConfigSerializable` instead of plain `ConfigSerializable` directly — it adds
the depth parameter your rendering needs, and gives you a `toConfig(ConfigEntry)` for
free that just calls your depth-aware version at the default depth.

## A complete example

A reward tier — an item material plus the permissions it grants — that a plugin author
wants as a single config field instead of three separate ones:

```java
public class RewardTier implements CompositeConfigSerializable, ConfigDeserializable {

    private String material;
    private List<String> permissions;

    public RewardTier(String material, List<String> permissions) {
        this.material = material;
        this.permissions = permissions;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "material", material)
                .appendCollection(depth, "permissions", permissions)
                .build();
    }

    @Override
    public void fromConfig(Object configObject) {
        if (configObject == null)
            return;
        ConfigurationSection section = (ConfigurationSection) configObject;
        material = section.getString("material", material);
        permissions = section.getStringList("permissions");
    }
}
```

Used as a field:

```java
@ConfigEntry(section = "rewards", path = "vip")
public static RewardTier vipTier =
        new RewardTier("DIAMOND", List.of("myplugin.vip", "myplugin.perks"));
```

Generates:

```yaml
rewards:
  vip: 
    material: 'DIAMOND'
    permissions:
      - 'myplugin.vip'
      - 'myplugin.perks'
```

(That trailing space after `vip:` is real — see below.) On reload, `Config` sees that
`RewardTier` implements `ConfigDeserializable` and calls `fromConfig` on the *existing*
instance rather than replacing the field, so `vipTier` must already be non-null when
`reloadConfig` runs — a `null` field is left alone, not constructed for you.

## `ConfigBuilder`

`ConfigBuilder` is the standard way to implement `toConfig`: accumulate `path: value`
lines, then render them as indented YAML text.

Every append call takes an explicit depth, or you can use the shorter overload that
defaults to `ConfigBuilder.DEFAULT_DEPTH` (`2`). That default matches the shape a
generated config actually has — a top-level section is depth 0, an item under it is
depth 1, and a plain field under that item is depth 2 — so you'll reach for
`DEFAULT_DEPTH` far more often than a custom depth.

| Method | Behavior |
|---|---|
| `append(path, value)` | Quotes a `String` with single quotes; a `Collection` is delegated to `appendCollection`; anything else uses `toString()`. `null` value = key omitted, not written empty. |
| `appendString(path, value)` | Writes `value` raw and unquoted, even a `String`. `null` = omitted. |
| `appendIfNotEqual(path, value, otherValue)` | Same quoting as `append`, but also a no-op when `value.equals(otherValue)` — use it to skip writing a field that's still at some baseline. |
| `appendCollection(path, value)` | See below. |
| `appendComposite(depth, path, value, configEntry)` | Renders another `CompositeConfigSerializable` at `depth + 1` and splices it in as raw text. Always needs an explicit depth — there's no default-depth overload for this one. |

Building the final string: `build()` (leading and trailing newline), `buildNoSpace()`
(no leading newline), `buildNoTrail()` (no trailing newline). Use `build()` for a value
that's the whole content of a top-level field — as in `RewardTier` above — and
`buildNoTrail()`/`buildNoSpace()` when your output is being spliced into something else's
builder, most often via `appendComposite`.

## `appendCollection`'s quoting asymmetry

This is the one behavior in this package most likely to surprise you. `appendCollection`
treats a collection differently depending on its size:

- **Empty** — no-op. The key is omitted entirely, not written as `[]` or `null`.
- **Exactly one element** — written inline as `[element]`, **unquoted even if the
  element is a `String`**.
- **Two or more elements** — one `- ` line per element, `String` elements quoted with
  single quotes.

So a single-permission `RewardTier` and a two-permission one produce visibly different
YAML for the same field type:

```yaml
# one permission
permissions: [myplugin.vip]

# two permissions
permissions:
  - 'myplugin.vip'
  - 'myplugin.perks'
```

Both re-parse the same way through `ConfigurationSection#getStringList`, so it's
harmless functionally — but don't be surprised when a config you generated with one item
in a list doesn't look like a trimmed-down version of the multi-item form. If your type
always needs a consistent look regardless of count, build the lines yourself with
`appendString` instead of `appendCollection`.

## See also

- [Configuration](config.md) — the annotation and generation model these interfaces plug
  into, including how a value's rendered text ends up quoted or not at the *field* level.
- [Configurables](../configurables/configurables.md) — a library of types already built
  on this contract (items, GUIs, ranges, durations, ...), and a good source of more
  examples of `toConfig`/`fromConfig` in practice.
- [Writing Your Own Configurable](../configurables/custom.md) — a related but different
  extension point: adding a new YAML key to an *existing* item/interaction/animation/GUI
  type, rather than inventing a whole new field type from scratch.
