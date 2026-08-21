# Value Configurables

These are the small ones. A value configurable wraps exactly one parsed value, so a
config field can be a range, a duration or a material instead of a `String` you re-parse
every time you use it. You declare the field once, the server owner writes `1..3` or
`30s`, and you read back a real object.

Reach for one whenever a config value has a grammar. Skip it for a plain `int`, `String`
or `boolean` — [`config`](../config/config.md) already handles those directly, and
wrapping them buys you nothing.

## What you need

All of these live in `wutils-configurables`. See [Configurables](configurables.md) for
the Gradle coordinates. None of them need an optional dependency — unlike items, GUIs
and interactions, the value configurables are pure parsing and pull in nothing beyond
`wutils-common`.

## Quick start

Declare the field, initialised with the default you want:

```java
@ConfigEntry(section = "spawner")
public static IntRangeConfigurable mobCount = new IntRangeConfigurable(new ClosedIntRange(1, 3));

@ConfigEntry(section = "spawner")
public static TimeSpanConfigurable cooldown = new TimeSpanConfigurable("30s");
```

That generates:

```yaml
spawner:
  mobCount: 1..3
  cooldown: 30s
```

And you read the parsed value back through the getter:

```java
int count = Config.mobCount.getRange().getRandom();
long ticks = Config.cooldown.getTimeSpan().getTicks();
```

Every class here has three constructors: no-arg (a hardcoded default), from a `String`
in the config's own grammar, and from an already-built value.

## Initialise the field — always

**A null field is skipped entirely on load.** Reloading calls `fromConfig` on the field's
*current value*, so if the field is null there is no object to parse into and your config
value is silently ignored.

```java
// Wrong — stays null forever, config value never lands.
public static TimeSpanConfigurable cooldown;

// Right — the instance is what gets populated.
public static TimeSpanConfigurable cooldown = new TimeSpanConfigurable("30s");
```

This is the single most common way to get "my config value isn't being read". See
[reading values back](../config/config.md) for the mechanics.

## The scalar types

Each takes one YAML scalar and hands parsing to a `common` helper. Generated config is
the value's own `toString()`, so all of them round-trip.

| Class | YAML | Getter returns | Default if never loaded |
|---|---|---|---|
| `IntRangeConfigurable` | `1..10` | `ClosedIntRange` | `0..1` |
| `DoubleRangeConfigurable` | `0.5..2.5` | `DoubleRange` | `0.0..1.0` |
| `VectorRangeConfigurable` | `-1,-1,-1..1,1,1` | `VectorRange` | a unit box at origin |
| `TimeSpanConfigurable` | `1m30s` | `TimeSpan` | `0` ticks |
| `MaterialConfigurable` | `DIAMOND_SWORD` | `Material` (nullable) | `STONE` |
| `IntComparatorConfigurable` | `>=5` | `IntComparator` | `= 0` |
| `DoubleComparatorConfigurable` | `<2.5` | `DoubleComparator` | `= 0` |
| `IntOperationConfigurable` | `*2` | `IntOperation` | `set 0` |
| `DoubleOperationConfigurable` | `+0.5` | `DoubleOperation` | `set 0` |

The getters are named after what they hold: `getRange()`, `getTimeSpan()`,
`getMaterial()`, `getIntComparator()`, `getDoubleOperation()`, and so on.

For the grammars themselves — what `1m30s` accepts, what a comparator or operation can
express — see [Ranges, Durations and Values](../common/values.md). Comparators and
operations are worth knowing about: they let a server owner write the *rule*
(`>=5`, `*2`) rather than making you invent config keys for it.

`VectorRangeConfigurable` is the one that does not round-trip its text exactly: a
hand-written `-1,-1,-1..1,1,1` regenerates as `-1.0,-1.0,-1.0..1.0,1.0,1.0`. It re-parses
identically, so this is cosmetic — but do not be surprised when regeneration rewrites
your file.

## Sounds

`SoundConfigurable` is the one section-valued member of the group:

```yaml
click:
  sound: 'minecraft:ui.button.click'   # namespaced key — NOT a Bukkit enum name
  source: MASTER
  volume: 1.0
  pitch: 1.0
```

**Prefer the `sound` attribute over this class.** `SoundConfigurable` builds its key and
its source directly, so it accepts only the namespaced form and only an exact-case
source: `sound: BLOCK_ANVIL_USE` throws, and omitting `sound` entirely throws too. The
shared sound attribute used by [items](items.md), [interactions](interactions.md) and
[animations](animations.md) accepts `BLOCK_ANVIL_USE` *or* `minecraft:block.anvil.use`,
is case-insensitive, and supports a one-line string form. Use this class only when you
need a standalone sound field outside any of those.

## Lists and maps

| Class | Holds | YAML |
|---|---|---|
| `ListConfigurable<E>` | `List<E>` | a sequence |
| `ListMapConfigurable<E>` | `Map<String, List<E>>` | a section of sequences |
| `GenericMapConfigurable<K, V>` | `Map<K, V>` | a section of scalars, through your own mappers |

```yaml
allowedWorlds:
  - world
  - world_nether

rewardsByRank:
  default:
    - 'give <player> bread 1'
  vip:
    - 'give <player> diamond 1'
    - 'give <player> emerald 4'
```

`getList()` and `getMap()` give you the parsed collection.

`GenericMapConfigurable` is the escape hatch for a map of a type nothing else covers. It
takes two mapper functions — one turning your `(K, V)` entry into the `(String, String)`
written to config, one turning a `(String, Object)` read back into a `(K, V)`:

```java
@ConfigEntry(section = "shop")
public static GenericMapConfigurable<Material, Integer> prices =
        new GenericMapConfigurable<>(
                entry -> MapUtils.entry(entry.getKey().name(), entry.getValue().toString()),
                entry -> MapUtils.entry(Material.matchMaterial(entry.getKey()),
                                        Integer.parseInt(entry.getValue().toString())));
```

Both mappers are constructor arguments, so there is no no-arg constructor — you *must*
initialise the field with its mappers. That is the same rule as every other class here,
just impossible to forget.

`ListOfConfigurables` is deprecated. Use a `ListMapConfigurable`, or a purpose-built
configurable of your own — see [Writing Your Own Configurable](custom.md).

## Sharp edges

- **Generating a config with an empty `ListConfigurable` crashes.** `toConfig` inspects
  an arbitrary element to decide whether to quote, which throws on an empty list. An
  empty list is an ordinary state for a config field, so give the field a non-empty
  default, or make sure something is loaded before you generate.
- **`MaterialConfigurable` accepts a bad material name silently.** An unrecognised name
  is stored as `null`, and you find out later — a `NullPointerException` when the config
  is written back out, or wherever you first use the material. Note this differs from the
  material *attribute* used by [item configurables](items.md), which rejects a bad name at
  load time and tells you the config path. Null-check `getMaterial()` if the value is
  server-owner-supplied.
- **The wrong YAML type is a `ClassCastException` during load.** Every class here casts
  the incoming value without checking it first, and the resulting error names neither the
  key nor the file. If config loading dies with an unexplained `ClassCastException`, look
  for a value configurable whose key was written as the wrong shape — a scalar where a
  list belongs, or the reverse.
- **Removing a key from config does not reset the field.** A null config value is ignored
  and the field keeps whatever it already had. After a reload that dropped the key, you
  are still holding the old value, not the constructor default. Delete-and-reload is not
  a way to restore defaults.

## See also

- [Configurables](configurables.md) — the module overview and the other family of
  configurable.
- [Configuration](../config/config.md) — how these fields get registered, generated and
  reloaded.
- [Ranges, Durations and Values](../common/values.md) — the grammars and the parsed types
  themselves.
- [Writing Your Own Configurable](custom.md) — when nothing here fits.
