# Serialization

`me.wyne.wutils.config.configurable` is the other half of the [`config`](config.md)
module: a small interface hierarchy a value can implement so it knows how to render
itself into the YAML `config` generates, plus `ConfigBuilder`, the helper used to build
that YAML text.

## The interface chain

Three interfaces, each adding capability to the last:

| Interface | Adds | File |
|---|---|---|
| `ConfigSerializable` | `toConfig(ConfigEntry)` — render to YAML text. Write-only. | `config/src/main/java/me/wyne/wutils/config/configurable/ConfigSerializable.java` |
| `ConfigDeserializable` | extends `ConfigSerializable`; adds `fromConfig(Object)` — repopulate this instance from a read-back config value. Read-write. | `config/src/main/java/me/wyne/wutils/config/configurable/ConfigDeserializable.java` |
| `CompositeConfigSerializable` | extends `ConfigSerializable`; adds a depth-aware `toConfig(int depth, ConfigEntry)`, for a value that needs to render nested *inside* another configurable's YAML rather than only on its own. | `config/src/main/java/me/wyne/wutils/config/configurable/CompositeConfigSerializable.java` |

A field whose current value implements `ConfigSerializable` but not
`ConfigDeserializable` is written by `Config#generateConfig` but silently skipped by
`Config#reloadConfig`/`loadConfig` — see [config.md](config.md#reading-values-back-reloadconfig-and-loadconfig).
That is the intended use for values built via a factory rather than mutated in place:
serialize-only, by design.

`CompositeConfigSerializable.toConfig(ConfigEntry)` (`CompositeConfigSerializable.java:20-23`)
is a default method that just calls `toConfig(ConfigBuilder.DEFAULT_DEPTH, configEntry)`
— so a composite configurable still satisfies the plain `ConfigSerializable` contract
when rendered on its own, at the builder's default nesting depth.

## `ConfigBuilder`

`ConfigBuilder` (`config/src/main/java/me/wyne/wutils/config/configurable/ConfigBuilder.java`)
accumulates an ordered set of `path: value` lines and renders them as indented YAML
text. It is the standard way to implement `toConfig`.

Values are keyed internally by `(depth, path)`: appending the same path at the same
depth twice **overwrites the value but keeps the original position** — the line's spot
in the output is fixed by its first append, not its last.

### Why the default depth is 2

`ConfigBuilder.DEFAULT_DEPTH` (`ConfigBuilder.java:32`) is `2`, matching the shape a
`config`-generated file actually has: a top-level `section:` key sits at depth 0, an
`item:` key nested under it sits at depth 1, and a `parameter: 5` line nested under
that item sits at depth 2.

A configurable's fields normally render as depth-2 lines — nested one level under an
"item" key, which is itself nested one level under a top-level "section" key. Depth is
only something a caller reaches for explicitly when building something with a
different shape, e.g. `appendComposite`'s "one level deeper than the caller" nesting
(below).

### The `append` family

| Method | Behavior |
|---|---|
| `append(path, value)` | Quotes a `String` value with single quotes; a `Collection` value is delegated to `appendCollection`; anything else uses `toString()`. Null value = no-op (key omitted, not written empty/null). |
| `appendString(path, value)` | Writes `value` **raw, unquoted** — even a `String` that looks like it needs quoting. Null value = no-op. |
| `appendIfNotEqual(path, value, otherValue)` | Same quoting as `append`, but also a no-op when `value.equals(otherValue)` — for omitting a field from generated YAML when it's still at some baseline/default. |
| `appendCollection(path, value)` | See below. |
| `appendComposite(depth, path, value, configEntry)` | Renders a `CompositeConfigSerializable` at `depth + 1` and appends the result as a raw string via `appendString`; a no-op if that render comes back empty. |

Every method takes an explicit depth; `append`, `appendString`, `appendIfNotEqual` and
`appendCollection` each also have a shorter overload that fixes `depth = DEFAULT_DEPTH`.
`appendComposite` is the exception — it has no such convenience overload, so a caller
must always pass its depth. Null values are silently omitted everywhere in this
family — the result is an absent key, never a key with a `null` or empty value.

### `appendCollection`'s quoting asymmetry

`appendCollection(depth, path, value)` (`ConfigBuilder.java:125-142`) has three separate
paths, and the two non-empty ones quote `String` elements differently:

- **Empty collection** — no-op, key omitted entirely.
- **Exactly one element** — written inline as `[element]` via `appendString`, i.e. raw
  and **unquoted**, even if the element is a `String`.
- **Two or more elements** — each rendered on its own `- ` line; elements are quoted
  with single quotes if (checked on an arbitrary element of the collection) they are
  `String`s.

So a single-element `String` collection and a multi-element `String` collection
produce different YAML for the same element type: `[hello]` versus
`- 'hello'\n- 'world'`. Know this before assuming a one-item list will look like a
trimmed-down multi-item one.

### Building the final text

Three `build` variants, differing only in leading/trailing newlines
(`ConfigBuilder.java:159-204`):

| Method | Leading newline | Trailing newline |
|---|---|---|
| `build()` | yes | yes |
| `buildNoSpace()` | no | yes |
| `buildNoTrail()` | yes | no |

All three render an empty string if nothing was appended, and otherwise emit every
line in insertion order (per `valueSequence`, not affected by later overwrites),
indented by `depth * 2` spaces.

<!-- allow-code-fences -->

## Working example

Every behavior described above shows up in one builder. A configurable rendering an
item might do this:

```java
record Sound(String key, float volume) implements CompositeConfigSerializable {
    public String toConfig(int depth, ConfigEntry e) {
        return new ConfigBuilder()
                .append(depth, "key", key)
                .append(depth, "volume", volume)
                .buildNoTrail();
    }
}

ConfigBuilder b = new ConfigBuilder();
b.append("material", "DIAMOND_SWORD");
b.append("amount", 1);
b.appendIfNotEqual("glowing", false, false);
b.appendIfNotEqual("unbreakable", true, false);
b.appendCollection("lore", List.of("Line one", "Line two"));
b.appendCollection("flags", List.of("HIDE_ATTRIBUTES"));
b.appendComposite(ConfigBuilder.DEFAULT_DEPTH, "sound", new Sound("entity.player.levelup", 1.0f), e);
b.append("material", "NETHERITE_SWORD");
b.build();
```

`build()` returns exactly this (leading blank line included; `·` marks a trailing
space that is really emitted):

```yaml

    material: 'NETHERITE_SWORD'
    amount: 1
    unbreakable: true
    lore:·
      - 'Line one'
      - 'Line two'
    flags: [HIDE_ATTRIBUTES]
    sound:·
      key: 'entity.player.levelup'
      volume: 1.0
```

Reading that output against the calls that produced it:

- **`material` appears once, at the top, with the *last* value.** It was appended
  first and again last; the second append overwrote the value but the line kept the
  position its first append gave it. This is the `(depth, path)` keying in action, and
  it is the one behavior most likely to surprise.
- **`glowing` is absent.** `appendIfNotEqual("glowing", false, false)` matched its
  reference value, so nothing was recorded — whereas `unbreakable` differed from its
  reference and was written.
- **`lore` is quoted, `flags` is not.** Both are `List<String>`; `lore` took the
  multi-element path (one `- ` line each, single-quoted) and `flags` took the
  single-element path (inline `[...]`, raw). This is the quoting asymmetry above,
  visible side by side.
- **Base indentation is four spaces**, because `DEFAULT_DEPTH` is 2 and lines are
  indented `depth * 2`. Collection items and the nested composite sit one level
  deeper at six.
- **`sound` renders its own builder's output** at `depth + 1`, spliced in as raw text
  via `appendString` — which is why the nested keys are not quoted or re-processed by
  the outer builder.
- **`lore:` and `sound:` carry a trailing space** before their newline, since their
  value begins with a line break. Harmless in YAML, but it shows up in diffs of
  generated files.

## See also

- [WUtils Config](config.md) — the annotation/generation model this package's
  interfaces plug into, and the `String`-vs-collection quoting these builders produce
  ending up embedded in a `ConfigSection`'s rendered output.
- The `configurables` module (`me.wyne.wutils.config.configurables`) supplies 26
  ready-made `*Configurable` classes built on these interfaces — 18 implementing
  `CompositeConfigSerializable`, 17 implementing `ConfigDeserializable`, none
  implementing bare `ConfigSerializable` alone. It will get its own wiki pages later.
