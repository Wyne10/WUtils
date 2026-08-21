# Ranges, Durations and Values

Config files are text. This page covers the parsers that turn a config string into a
typed value your code can actually use — ranges, durations, cooldowns, comparators and
operations — plus a handful of small standalone utilities (`Args`, `MapUtils`) they're
built on, and `ConfigUtils`, which wires the parsers up to a Bukkit
`ConfigurationSection` directly. Most of the traps on this page are silent — wrong
behavior with no exception — so read the "gets you" callouts before you ship a config
schema built on these.

## Ranges

Five range types share one shape — minimum, maximum, random sampling, containment,
iteration — over different value types:

| Type | Value | Config string format |
|---|---|---|
| `ClosedIntRange` | `Integer` | `min..max` |
| `DoubleRange` | `Double` | `min..max` |
| `TimeSpanRange` | `TimeSpan` (see below) | `min..max`, each a duration string like `5s` |
| `VectorRange` | `Vector` | `minX,minY,minZ..maxX,maxY,maxZ` |
| `LocationRange` | `Location` | `world minX,minY,minZ..maxX,maxY,maxZ` |

```java
import me.wyne.wutils.common.range.ClosedIntRange;
import me.wyne.wutils.common.range.VectorRange;

ClosedIntRange amount = ClosedIntRange.getIntRange("3..7");
int rolled = amount.getRandom(); // uniformly in [3, 7], inclusive

VectorRange region = VectorRange.getVectorRange("0,0,0..15,255,15");
```

**Every range is closed on both ends, for both `contains()` and `getRandom()`** —
`3..7` can roll a `7`, and `7` satisfies `contains()`. That's true whether the value type
is discrete or continuous, but the two get there differently under the hood; the
behavior you should rely on is just: both bounds are always includable.

### Building a range without going through the string parser

The string factories (`getIntRange`, `getVectorRange`, etc.) always produce a valid,
correctly-ordered range no matter which corner you list first in config. The plain
constructors are less forgiving for a couple of shapes:

**`VectorRange`/`LocationRange`'s center+radius constructor names a box, not a sphere.**

```java
// Wrong — reads like "everything within 10 blocks of center", but it's a cube:
VectorRange nearby = new VectorRange(center, 10.0);
if (nearby.contains(playerLocation.toVector())) { ... } // true even at the cube's corners, ~14 blocks away

// Right — if you actually want a sphere, check distance yourself:
boolean inRange = center.distance(playerLocation.toVector()) <= 10.0;
```

`contains()` on a center+radius `VectorRange` is the same axis-aligned box check as any
other `VectorRange` — there's no distance math involved despite the parameter name.

**A negative width/height/depth/radius silently builds an unusable range**, because
these constructors don't normalize:

```java
// Wrong — negative height means min.y > max.y on that axis:
VectorRange broken = new VectorRange(center, 5.0, -3.0, 5.0);
broken.contains(anyPoint); // false, always — the Y check can never pass
```

The two-`Vector` constructor (`new VectorRange(min, max)`) *does* normalize per axis
regardless of argument order, and so does every string factory — this trap is specific
to the center+dimensions constructors.

## Durations and ticks

### There are two classes named `Ticks` — know which one you're importing

| Class | What it's for |
|---|---|
| `me.wyne.wutils.common.Ticks` | static tick↔millisecond/second conversions — the one you call directly |
| `me.wyne.wutils.common.duration.Ticks` | one `Duration` *unit* among six (ms/s/m/h/d/ticks) — you reach it as `Durations.Ticks`, not by constructing it |

```java
import me.wyne.wutils.common.Ticks;

long ticks = Ticks.ofSeconds(30); // 600 — direct conversion, this is what you want most of the time
```

```java
// Wrong — building a TimeSpan by hand-constructing the unit instead of using the shared singleton:
import me.wyne.wutils.common.duration.TimeSpan;
TimeSpan span = new TimeSpan(30, new me.wyne.wutils.common.duration.Ticks());
Durations.getSymbol(span.type()); // returns null — this instance isn't Durations.Ticks

// Right — use the Durations constant:
import me.wyne.wutils.common.duration.Durations;
TimeSpan span = new TimeSpan(30, Durations.Ticks);
```

The reverse-lookup table in `Durations` is keyed by *instance*, and the unit classes
don't override `equals`. Always build a `TimeSpan` from a `Durations.*` constant
(`Durations.Seconds`, `Durations.Ticks`, ...), never a freshly-constructed unit object.

### Parsing duration strings

`Durations.getTimeSpan(string)` / `getMillis(string)` / `getTicks(string)` parse strings
like `1h30m`, `5s`, `200ms`, `100` (bare numbers mean **ticks**, not milliseconds):

```java
import me.wyne.wutils.common.duration.Durations;

long cooldownTicks = Durations.getTicks("5s"); // 100
```

A leading `-` is stripped rather than negated (durations are always non-negative), and
junk between number/unit tokens is silently skipped rather than rejected — `1h!!!30m`
parses the same as `1h30m`. A string with **no digits at all** throws
`IllegalArgumentException` — that's the one case that actually fails loudly.

## Cooldowns

`Period` (one cooldown) and `CooldownMap<T>` (many, keyed by e.g. player `UUID`) store an
absolute finish timestamp and expose *remaining* time — neither ticks or schedules
anything, they're purely passive:

```java
import me.wyne.wutils.common.cooldown.CooldownMap;
import java.util.concurrent.TimeUnit;

CooldownMap<UUID> cooldowns = new CooldownMap<>(true); // ConcurrentHashMap-backed — use this if touched off-thread

cooldowns.put(player.getUniqueId(), 30, TimeUnit.SECONDS);

if (cooldowns.isCooldowned(player.getUniqueId())) {
    player.sendMessage("Wait " + cooldowns.getRemainingStringFormat(player.getUniqueId()));
    return;
}
```

Use `new CooldownMap<>(true)` rather than the plain no-arg constructor if cooldowns are
ever read or written from an async context (e.g. inside `Schedulers.async()` work — see
[Scheduling and Async Work](async.md)); the plain constructor is backed by a `HashMap`,
which isn't safe for concurrent access.

### `CooldownMap` never evicts expired entries

```java
// Over a long-running server, this line alone means the map grows forever:
cooldowns.put(player.getUniqueId(), 30, TimeUnit.SECONDS);
```

`isCooldowned` reports an expired entry as "not cooldowned" but leaves it sitting in the
map — nothing removes it except an explicit `remove(key)` call, or serializing the whole
map (see below), which drops expired entries as a side effect of saving. On a busy server
that never restarts and never saves cooldowns to disk, a map keyed by player UUID grows
without bound for the life of the process. If you don't already periodically persist
cooldowns, call `remove(key)` yourself once an entry expires, or serialize/deserialize on
an interval purely to prune.

## Comparators and operations

For config rules like "only if level >= 5" or "add 2 to the base value" without writing a
parser per feature:

```java
import me.wyne.wutils.common.comparator.Comparators;
import me.wyne.wutils.common.operation.Operations;

var minLevel = Comparators.getIntComparator(">=5");
boolean allowed = minLevel.compare(player.getLevel()); // left-hand value goes in

var bonus = Operations.getIntOperation("+2");
int result = bonus.evaluate(baseValue);
```

Both accept an optional leading operator (`<=`, `>=`, `==`, `<`, `>` for comparators;
`+`, `-`, `*`, `/`, `**` for operations) followed by a number; omitting the operator is
legal and means "equals" for a comparator or "set to" for an operation — a bare `"5"`
parses as "equal to 5" as a condition and "set to 5" as a transformation.

**An unrecognized operator doesn't fail — it silently falls back to a default that looks
nothing like an error:**

```java
Comparators.getIntComparator("~5"); // "~" isn't a real operator — parses as Equals, not a thrown exception
Operations.getIntOperation("~5");   // same story — parses as Set
```

If you're validating a config schema and want to catch a genuine typo instead of quietly
accepting it as "equals"/"set", check the parsed result's operator against what you
expected rather than trusting that parsing succeeded means the operator was valid.

**A string that doesn't match the format at all throws `IllegalStateException`**, not a
parse-error message naming the bad value — validate the raw string yourself if you want a
clearer failure for malformed config.

`Comparator`/`Operation` implementations for `int` and `double` are separate types
(`getIntComparator`/`getDoubleComparator`, `getIntOperation`/`getDoubleOperation`) — pick
the one matching your value's type.

## `Args` — splitting config strings

`Args` is a small quote-aware string splitter used throughout this module's own parsers
(dust-options, vector ranges) and available for your own config values:

```java
import me.wyne.wutils.common.Args;

Args args = new Args("#FF0000:2.0"); // default delimiter splits on ':' or whitespace
String color = args.get(0);          // "#FF0000"
String size = args.get(1, "1.0");    // "2.0" — or the default if index 1 doesn't exist
```

A double-quoted span survives splitting even if it contains a delimiter character, so
`"#surface #solid" fast` (a WorldEdit-style mask followed by another argument) comes back
as two arguments, not four — but the quotes themselves are consumed in the process, so
you can't get a literal `"` back out.

**`get(index)` trims the result; `getNullable(index)` doesn't.** If you're comparing
values read through both methods, don't be surprised by a mismatch that's really just
leading/trailing whitespace:

```java
// Wrong — comparing trimmed and untrimmed reads as if they're the same operation:
if (args.get(0).equals(args.getNullable(0))) { ... } // can be false even for the same index, if the raw text had padding

// Right — pick one accessor and use it consistently:
String value = args.get(0); // always trimmed, "" if missing
```

Use `get(index)` (returns `""` for a missing index) unless you specifically need to
distinguish "missing" from "empty string," in which case use `getNullable(index)` and
check for `null`.

## `MapUtils` — transforming maps while keeping order

`MapUtils.map(sourceMap, mapFunction)` transforms every entry into a new key/value pair.
The two-argument form always returns a `HashMap`, which **does not preserve iteration
order** — pass a `Supplier` explicitly if you need to keep the source's order:

```java
import me.wyne.wutils.common.MapUtils;
import java.util.LinkedHashMap;

// Wrong — if displayOrder matters, this silently reorders it:
var renamed = MapUtils.map(configuredRewards, (entry) ->
        MapUtils.entry(entry.getKey().toUpperCase(), entry.getValue()));

// Right — supply the destination map type explicitly:
var renamed = MapUtils.map(configuredRewards, (entry) ->
        MapUtils.entry(entry.getKey().toUpperCase(), entry.getValue()), LinkedHashMap::new);
```

If two source entries map to the same destination key, the transform silently keeps only
the last one written — the result can end up smaller than the input with no warning.

## `ConfigUtils` — reading typed values from a `ConfigurationSection`

`ConfigUtils` is a thin layer over the parsers above, reading directly from a Bukkit
`ConfigurationSection` instead of a bare string. **It's unrelated to the separate
[`config`](../config/config.md) module** — that one generates and writes whole YAML
files from annotated fields; `ConfigUtils` just reads values out of a section you already
have, no annotations involved.

```java
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;

TimeSpan cooldown = ConfigUtils.getTimeSpan(getConfig(), "cooldown",
        new TimeSpan(30, Durations.Seconds));
```

The `def`-taking overloads (`getTimeSpan(config, path, def)`, `getVector(config, path,
def)`, `getMillis`/`getTicks(config, path, def)`) fall back to the default when the value
is blank or missing. **The no-default overloads don't guard against a blank value at
all** — they hand the (possibly empty) string straight to the underlying parser and let
whatever exception it throws propagate. Prefer the `def`-taking overload unless you
specifically want a missing key to be a hard error.

`getEnumSet`/`getMaterialEnumSet` differ in how they handle an unmatched entry: enum sets
log a warning and skip it, material sets drop it with no log at all — if a material list
in your config seems to be missing entries, check for typos, since nothing will tell you.

## Gson serializers for types Gson can't handle alone

`me.wyne.wutils.common.serialization` has four Gson adapters, register them once on a
`GsonBuilder`:

```java
import com.google.gson.GsonBuilder;
import me.wyne.wutils.common.serialization.Base64ItemStackSerializer;
import me.wyne.wutils.common.serialization.CooldownMapSerializer;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

Gson gson = new GsonBuilder()
        .registerTypeAdapter(ItemStack.class, new Base64ItemStackSerializer())
        .registerTypeAdapter(CooldownMap.class, new CooldownMapSerializer<>(UUID.class))
        .create();
```

`Base64ItemStackSerializer`/`Base64InventorySerializer` round-trip an `ItemStack`/
`Inventory` through Bukkit's own Java serialization, Base64-encoded into a JSON string.
It captures everything including NBT, but it's **only safely readable by a server that
can deserialize the exact same classes** — treat it as a same-version storage format for
a live database, not a long-term archive or cross-server interchange format. A restored
`Inventory` also has no holder (it's built with `Bukkit.createInventory(null, size)`) —
only size and contents survive.

`PeriodSerializer`/`CooldownMapSerializer` persist **remaining time, not the absolute
timestamp**. A cooldown with 30 seconds left, saved and reloaded an hour later, still has
30 seconds left — which is what you want for a cooldown meant to survive a server
restart untouched, and the wrong choice for something meant to track wall-clock time
(a daily reward should use an absolute timestamp instead, not one of these). Saving a
`CooldownMap` also doubles as your only real eviction mechanism — see
[the never-evicts note above](#cooldowns) — since `CooldownMapSerializer` skips writing
expired entries.

## See also

- [Scheduling and Async Work](async.md) — converting durations to ticks for
  delayed/repeating scheduler calls.
- [Items, Players and Worlds](game-objects.md) — the particle parsers, which lean on
  `Args` the same way `DustOptionsParser` does.
- [WUtils Config](../config/config.md) — the separate, annotation-driven whole-file
  config module, not to be confused with `ConfigUtils`.
- contributor wiki pages with more depth:
  [Ranges](../../dev/common/ranges.md), [Durations and Cooldowns](../../dev/common/durations.md),
  [Comparators and Operations](../../dev/common/operations.md),
  [Core Utilities](../../dev/common/utilities.md), [Config Utilities](../../dev/common/config-utils.md),
  [Gson Serializers](../../dev/common/gson-serializers.md).
