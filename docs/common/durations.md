# Durations and Cooldowns

Three related things live here: a tick-conversion utility, a small unit system for
parsing human durations like `5m30s`, and two cooldown holders built on top.

## Two classes named `Ticks`

This is the first thing to get straight, because both are public and they do different
jobs:

| Class | What it is |
|---|---|
| `me.wyne.wutils.common.Ticks` (`common/src/main/java/me/wyne/wutils/common/Ticks.java:20`) | A static utility of tick↔time conversions. No instances. This is the one you call directly, and the one the vendored helper scheduler code imports. |
| `me.wyne.wutils.common.duration.Ticks` (`common/src/main/java/me/wyne/wutils/common/duration/Ticks.java:13`) | One `Duration` implementation among six, representing "the unit is ticks". You normally reach it as `Durations.Ticks`, not by name. |

The unit class delegates its arithmetic to the utility class
(`common/src/main/java/me/wyne/wutils/common/duration/Ticks.java:16-18`), so they never disagree — but importing the wrong one is
easy, and the compiler will happily accept either in a file that only uses the name
`Ticks`.

### The conversion basis

`MILLIS_PER_TICK = 50` and `TICKS_PER_SECOND = 20`
(`common/src/main/java/me/wyne/wutils/common/Ticks.java:22-23`), with
`TICKS_PER_MINUTE` and `TICKS_PER_HOUR` derived from those. Conversions are exact in
the tick-producing direction for whole seconds (`ofSeconds` is a multiply,
`common/src/main/java/me/wyne/wutils/common/Ticks.java:33-35`) and truncating toward
zero where a division is involved — `ofMillis` divides by 50
(`common/src/main/java/me/wyne/wutils/common/Ticks.java:28-30`), so 74ms becomes 1
tick, not 2.

## The unit system

`Duration` (`duration/Duration.java:13-20`) is an interface with three methods —
`getMillis(long)`, `getTicks(long)`, `getUnit(long, TimeUnit)` — and six
implementations, exposed as singletons on `Durations` (`duration/Durations.java:19-24`):

| Symbol | Constant | Unit |
|---|---|---|
| `ms` | `Durations.Millis` | milliseconds |
| `s` | `Durations.Seconds` | seconds |
| `m` | `Durations.Minutes` | minutes |
| `h` | `Durations.Hours` | hours |
| `d` | `Durations.Days` | days |
| `t` | `Durations.Ticks` | ticks |

Every unit converts exactly except `Millis.getTicks`, which truncates through
`ofMillis` (`duration/Millis.java:21-23`) — the only lossy conversion in the set.

`TimeSpan` (`duration/TimeSpan.java:16`) pairs an amount with a unit. It is a record
implementing `Duration` itself, so it has both no-argument accessors that use its own
stored amount (`getMillis()`, `getTicks()`, `getUnit(TimeUnit)`) and the interface's
one-argument forms that convert an arbitrary amount using its unit. Two similarly named
methods that mean different things — check which one you are calling.

`TimeSpan.toString()` (`duration/TimeSpan.java:53-78`) renders back to the parseable
symbol form: a tick-typed span prints as `<n>t`, a zero span as `0t`, and anything else
decomposes into `d`/`h`/`m`/`s`/`ms` parts, omitting zero components. So `90000ms`
prints as `1m30s`.

### Parsing duration strings

`Durations.getMillis(String)` (`duration/Durations.java:80-98`) matches
`DURATION_REGEX` — `(\d+)(ms|[smhdt])?`, case-insensitive
(`duration/Durations.java:26`) — repeatedly with `find()` and sums the parts, so
`1h30m` works. Points to know:

- **A number with no suffix means ticks.** `getDuration(null)` returns `Ticks`
  (`duration/Durations.java:46-49`), and the suffix group is optional, so `100` parses
  as 100 ticks, not 100 milliseconds.
- **Amounts are made absolute.** `Math.abs` is applied to each parsed number
  (`duration/Durations.java:87`), so a leading `-` is ignored rather than subtracting.
- **Junk between tokens is silently skipped**, because `find()` scans rather than
  requiring a full match. `1h!!!30m` parses as 1h30m, and so does `1 hour 30 minutes`
  — the `our` and `inutes` are simply not matched.
- **It does throw when nothing matches at all.** A string with no digits produces
  `IllegalArgumentException("Invalid duration: ...")` (`duration/Durations.java:94-96`).

`getTimeSpan`, `getTicks` and `getTimeSpanRange` (`duration/Durations.java:67-69`,
`duration/Durations.java:103-105`, `duration/Durations.java:111-113`) are conveniences
over the same parse; the last delegates to [`TimeSpanRange`](ranges.md).

### Two nullable lookups

`getDuration(String)` returns **null** for a non-blank symbol that is not one of the
six (`duration/Durations.java:46-49`); only null or blank input gets the `Ticks`
default. `getSymbol(Duration)` returns **null** for any `Duration` that is not one of
the six singletons (`duration/Durations.java:56-59`); only null input gets `"t"`.

That second one has a trap: the reverse map is keyed by instance, and the unit classes
do not override `equals`. A freshly constructed `new Ticks()` is not `Durations.Ticks`,
so `getSymbol` returns null for it. Always use the `Durations` constants.

## Cooldowns

Both cooldown types store an **absolute finish timestamp** internally and expose
*remaining* time. Neither one ticks or schedules anything — they are passive, and
expiry is only ever observed by asking.

`Period` (`cooldown/Period.java:15`) is a single cooldown. Constructors take a raw
finish time, a duration plus `TimeUnit`, or a `TimeSpan`
(`cooldown/Period.java:23-33`); `put` in the same three shapes restarts it
(`cooldown/Period.java:46-56`); `stop()` clears it (`cooldown/Period.java:59-61`).

`getFinishAt()` is **nullable** (`cooldown/Period.java:36-38`) and null is a normal
state, not an error: the no-argument constructor never sets the field
(`cooldown/Period.java:20`) and `stop()` sets it back to null. `isExpired()` treats
null as expired (`cooldown/Period.java:41-43`), so "never started" and "finished" are
indistinguishable through the public API.

`CooldownMap<T>` (`cooldown/CooldownMap.java:23`) is the same idea keyed by `T` —
typically a player `UUID`. `new CooldownMap()` uses a `HashMap`;
`new CooldownMap(true)` uses a `ConcurrentHashMap` (`cooldown/CooldownMap.java:28-36`),
which is the constructor to use if cooldowns are touched off the main thread.

`getRemaining` returns 0 rather than a negative number once expired
(`cooldown/CooldownMap.java:74-79`), `getRemainingDuration` wraps that in a
millisecond-typed `TimeSpan` (`cooldown/CooldownMap.java:85-87`), and the two
`getRemainingStringFormat` overloads format via commons-lang3's `DurationFormatUtils`,
defaulting to `HH:mm:ss` (`cooldown/CooldownMap.java:89-96`).

### `CooldownMap` never evicts

`isCooldowned` reports an expired entry as not cooldowned but leaves it in the map
(`cooldown/CooldownMap.java:46-48`), and nothing else removes it except an explicit
`remove(key)` (`cooldown/CooldownMap.java:64-66`). A map keyed by player UUID on a busy
server therefore grows without bound for the lifetime of the process.

In practice the thing that clears it is saving: `CooldownMapSerializer` skips
non-cooldowned entries when writing, so a save/load cycle prunes them. See
[Gson Serializers](gson-serializers.md), which also explains why both cooldown types
persist *remaining* time rather than the absolute timestamp they hold in memory.

`getAsPeriod` (`cooldown/CooldownMap.java:69-71`) converts one entry to a `Period`,
defaulting a missing key to finish-time 0 — i.e. an already-expired period rather than
a null.

## See also

- [WUtils Common](common.md) — module overview.
- [Ranges](ranges.md) — `TimeSpanRange` iterates between two `TimeSpan`s.
- [Gson Serializers](gson-serializers.md) — persisting `CooldownMap` and `Period`.
- [Vendored helper Library](helper.md) — the scheduler stack, which imports the root
  `Ticks` utility for its own conversions.
