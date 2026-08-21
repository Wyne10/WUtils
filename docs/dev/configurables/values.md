# Value Configurables

These are the small ones: a configurable that wraps exactly one parsed value, so a
`@ConfigEntry` field can be a range or a duration instead of a `String` you re-parse at
every use.

All of them live directly in
`configurables/src/main/java/me/wyne/wutils/config/configurables/`, and all of them
follow the same three-constructor shape — empty (with a hardcoded default), from the
config value, and from an already-built value.

<!-- allow-code-fences -->

## Usage

```java
@ConfigEntry(section = "spawner")
public static IntRangeConfigurable mobCount = new IntRangeConfigurable(new ClosedIntRange(1, 3));

@ConfigEntry(section = "spawner")
public static TimeSpanConfigurable cooldown = new TimeSpanConfigurable("30s");
```

```yaml
spawner:
  mobCount: 1..3
  cooldown: 30s
```

```java
int count = Config.mobCount.getRange().getRandom();
```

The field must be **initialised**, not left null: `config`'s reload path calls
`fromConfig` on the field's *current value*, so a null field is skipped entirely. See
[WUtils Config](../config/config.md#reading-values-back-reloadconfig-and-loadconfig).

## The string-valued ones

Each takes a single scalar and delegates parsing to a `common` helper. Generated config
is the value's `toString()`, so all of these round-trip.

| Class | YAML | Parsed to | Default if never loaded |
|---|---|---|---|
| `IntRangeConfigurable` | `1..10` | `ClosedIntRange` | `0..1` |
| `DoubleRangeConfigurable` | `0.5..2.5` | `DoubleRange` | `0.0..1.0` |
| `VectorRangeConfigurable` | `-1,-1,-1..1,1,1` | `VectorRange` | a unit box at origin |
| `TimeSpanConfigurable` | `1m30s` | `TimeSpan` | `0` ticks |
| `MaterialConfigurable` | `DIAMOND_SWORD` | `Material` | `STONE` |
| `IntComparatorConfigurable` | `>=5` | `IntComparator` | `= 0` |
| `DoubleComparatorConfigurable` | `<2.5` | `DoubleComparator` | `= 0` |
| `IntOperationConfigurable` | `*2` | `IntOperation` | `set 0` |
| `DoubleOperationConfigurable` | `+0.5` | `DoubleOperation` | `set 0` |

See [Ranges](../common/ranges.md), [Durations and Cooldowns](../common/durations.md) and
[Comparators and Operations](../common/operations.md) for the grammars and for what the
parsed objects can do. Verified: `3..7`, `0.5..2.5`, `1m30s`, `>=5`, `*2`, `+0.5` and `<2.5`
all re-render as exactly the text they were written as. `VectorRangeConfigurable` is the
one exception — a hand-written `-1,-1,-1..1,1,1` regenerates as
`-1.0,-1.0,-1.0..1.0,1.0,1.0`. It re-parses identically; only the file text changes.

## SoundConfigurable

The one section-valued member of this group
(`configurables/src/main/java/me/wyne/wutils/config/configurables/SoundConfigurable.java`):

```yaml
click:
  sound: 'minecraft:ui.button.click'   # a namespaced key — NOT a Bukkit enum name
  source: MASTER
  volume: 1.0
  pitch: 1.0
```

**Prefer the `sound` attribute over this class.** `SoundConfigurable.fromConfig`
(`SoundConfigurable.java:51-60`) builds its key with `Key.key(config.getString("sound"))`
and its source with `Sound.Source.valueOf(...)` directly, so it accepts only the
namespaced form and only an exact-case source. The
[shared `SoundAttribute`](attributes.md) goes through `ConfigUtils.getByKeyOrName`, so it
accepts `BLOCK_ANVIL_USE` *or* `minecraft:block.anvil.use`, is case-insensitive, and also
supports a one-line string form.

Verified: `sound: BLOCK_ANVIL_USE` throws `InvalidKeyException` here, and omitting
`sound` entirely throws `NullPointerException`.

## The collection ones

| Class | Holds | YAML |
|---|---|---|
| `ListConfigurable<E>` | `List<E>` | a YAML sequence |
| `ListMapConfigurable<E>` | `Map<String, List<E>>` | a section of sequences |
| `GenericMapConfigurable<K, V>` | `Map<K, V>` | a section of scalars, through caller-supplied mappers |

```yaml
# ListConfigurable<String>
allowedWorlds:
  - world
  - world_nether

# ListMapConfigurable<String>
rewardsByRank:
  default:
    - 'give <player> bread 1'
  vip:
    - 'give <player> diamond 1'
    - 'give <player> emerald 4'
```

`GenericMapConfigurable` (`GenericMapConfigurable.java:25-76`) is the escape hatch for a
map of a type nothing else covers. It takes two
[`MapUtils.MapFunction`](../common/utilities.md)s — one turning a `(K, V)` entry into the
`(String, String)` written to config, one turning a `(String, Object)` read back into a
`(K, V)`:

```java
public static GenericMapConfigurable<Material, Integer> prices =
        new GenericMapConfigurable<>(
                entry -> MapUtils.entry(entry.getKey().name(), entry.getValue().toString()),
                entry -> MapUtils.entry(Material.matchMaterial(entry.getKey()),
                                        Integer.parseInt(entry.getValue().toString())));
```

Because both mappers are constructor arguments, it has no no-arg constructor — the field
must be initialised with its mappers before the first config read, which is the same
requirement as every other configurable here, just more visibly.

`ListOfConfigurables` (`ListOfConfigurables.java:18-19`) is `@Deprecated`. It stored a
list of configurables rendered as inline strings; a `ListMapConfigurable` or a
purpose-built `AttributeConfigurable` covers it better.

## Sharp edges

- **`ListConfigurable.toConfig` throws on an empty list.** It calls
  `list.stream().findAny().get()` to decide whether to quote the elements
  (`ListConfigurable.java:41-55`), which is `NoSuchElementException` when the list is
  empty. Verified. An empty list is a perfectly ordinary state for a config field, so
  generating a config before anything is loaded can crash.
- **`MaterialConfigurable` accepts an invalid material silently, then throws later.**
  `Material.matchMaterial` returns null, which is stored as-is
  (`MaterialConfigurable.java:37-42`); the failure surfaces as a
  `NullPointerException` from `toConfig`, or wherever the material is first used.
  Verified. Note the divergence from the item attribute of the same name:
  [`MaterialAttribute`](items.md) rejects an unrecognised name at load with the config
  path, while this class stores the `null` and fails later somewhere else.
- **The `fromConfig` casts are unchecked.** Every class here casts the incoming
  `Object` — `(String)`, `(List<E>)`, `(ConfigurationSection)` — with no `instanceof`
  guard. A key written as the wrong YAML type is a `ClassCastException` during config
  load, naming neither the key nor the file.
- **A `null` config value is silently ignored, keeping the previous value.** Every
  `fromConfig` here returns early on null (e.g. `IntRangeConfigurable.java:36-41`). After
  a reload that removed the key, the field keeps whatever it had — it does not revert to
  the constructor default.

## See also

- [WUtils Config](../config/config.md) — how `@ConfigEntry` fields are registered,
  generated and reloaded.
- [Ranges](../common/ranges.md), [Durations and Cooldowns](../common/durations.md),
  [Comparators and Operations](../common/operations.md) — the value types being wrapped.
- [Attributes and Containers](attributes.md) — the other family of configurable, for
  anything with more than one field.
