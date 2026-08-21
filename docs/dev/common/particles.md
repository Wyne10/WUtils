# Particle Data Parsers

`me.wyne.wutils.common.particle` converts a configuration string into the *data*
object a Bukkit particle needs — the `Material` for a block-crack particle, the
`Particle.DustOptions` for coloured dust, and so on. It exists so a particle can be
fully described in a config file, and it is the mechanism the
[`animation`](../animation/animation.md) module's particle runnables rely on.

Each parser also supplies tab-completion suggestions and can render a data object
back to a string, so the same class serves reading config, completing a command
argument, and writing config back out.

## The interface

`StringDataParser<T>` (`StringDataParser.java:11-25`) has three members:

| Member | Purpose |
|---|---|
| `getSuggestions()` | Candidate strings for tab completion. Some parsers enumerate an enum; others return a single syntax hint like `<int>`. |
| `getData(String)` | Parse a string into a `T`. Declared `@Nullable` because one implementation genuinely returns null. |
| `toString(Object)` | Render a data object back to its config form. Takes `Object`, not `T`. |

`toString` taking `@Nullable Object` rather than `T` is worth noticing — it is not
type-safe, and every implementation immediately casts. See the sharp edges below.

## Choosing a parser

`DataParserProvider.getDataParser(Class)` (`DataParserProvider.java:38-41`) is the
lookup. It holds a fixed map of nine entries, keyed by the data class:

`Void`, `Material`, `BlockFace`, `Potion`, `Integer`, `Color`, `ItemStack`,
`BlockData`, `Particle.DustOptions` (`DataParserProvider.java:25-35`).

Two things about the lookup matter:

- **It matches on the exact class only.** The map is a plain `HashMap` and the method
  is a plain `get`. A subclass, or `Integer.TYPE` instead of `Integer.class`, misses.
- **It returns null for anything unregistered**, with no exception and no logging. A
  caller that forgets to null-check gets an NPE at some later, less obvious point.

## What each parser accepts, and how it fails

Failure mode is the whole story here — the eleven parsers are inconsistent about it,
and a malformed config value will produce a different outcome depending only on which
data type the particle happens to use.

| Parser | Accepts | On malformed input |
|---|---|---|
| `VoidDataParser` (`VoidDataParser.java:19-21`) | anything; suggestions are empty | returns `null` always — it is a no-op for particles that take no data |
| `MaterialParser` (`MaterialParser.java:22-24`) | a material name, via `Material.matchMaterial` | **returns `null`** — the only parser that reports failure by value |
| `BlockDataParser` (`BlockDataParser.java:26-28`) | a block material name | `NullPointerException` — `matchMaterial` returns null and `createBlockData()` is called on it |
| `ItemStackParser` (`ItemStackParser.java:24-26`) | a material name | `IllegalArgumentException` — the `ItemStack` constructor rejects the null material |
| `BlockFaceParser` (`BlockFaceParser.java:22-24`) | an exact `BlockFace` constant name | `IllegalArgumentException` from `Enum.valueOf` |
| `PotionParser` (`PotionParser.java:24-26`) | an exact `PotionType` constant name | `IllegalArgumentException` from `Enum.valueOf` |
| `IntegerParser` (`IntegerParser.java:21-23`) | a decimal integer | `NumberFormatException` |
| `ColorParser` (`ColorParser.java:31-41`) | a `Color` constant name, else hex RGB with an optional leading `#` | `NumberFormatException` on unparseable hex |
| `DustOptionsParser` (`DustOptionsParser.java:29-34`) | `<colour>:<size>`, colon- or space-separated, both parts optional | `NumberFormatException` if a part is present but malformed |

`MaterialParser` returning null where `BlockDataParser` and `ItemStackParser` throw is
the asymmetry to remember: all three accept the same input — a material name — and all
three are fed from the same suggestion list, but only one of them fails softly.

### The two parsers with real syntax

**`ColorParser`** tries the named-constant path first, and only if the string is *not*
in its suggestion list does it fall through to hex (`ColorParser.java:32-41`). Its
suggestions are computed reflectively from `Color`'s static fields, excluding
`BIT_MASK` (`ColorParser.java:19-22`). `toString` renders a colour as its decimal RGB
integer (`ColorParser.java:44-46`), *not* as hex and not as a constant name — so a
config value written as `RED` or `#FF0000` comes back as `16711680` after a
round-trip. All three forms re-parse correctly, but the file text changes.

**`DustOptionsParser`** splits with [`Args`](utilities.md), so both `#FF0000:2.0` and
`#FF0000 2.0` work. Both parts have defaults — colour `0` (black) and size `1.0`
(`DustOptionsParser.java:31-32`) — so an empty string parses successfully into black
dust at default size rather than failing. Its single suggestion is the syntax hint
`<color(24bit rgb):size(float)>` (`DustOptionsParser.java:21`).

## Sharp edges

**`toString` will NPE on null in eight of nine implementations.** The interface
declares the parameter `@Nullable` (`StringDataParser.java:25`), but every parser
except `VoidDataParser` casts and dereferences immediately — `MaterialParser.java:29`,
`BlockDataParser.java:33`, `ItemStackParser.java:31`, `BlockFaceParser.java:29`,
`PotionParser.java:31`, `IntegerParser.java:28`, `ColorParser.java:45` and
`DustOptionsParser.java:38`. Only `VoidDataParser.java:26` honours the contract by
returning an empty string. Either the interface should narrow the parameter to
non-null or the implementations should guard; as it stands the annotation promises
something the code does not deliver.

**`toString` accepts any `Object`.** Because the parameter is not `T`, passing the
wrong data type to the wrong parser compiles fine and fails with a `ClassCastException`
at runtime.

**`PotionParser` is deprecated territory.** It produces a `Potion`, and
`DataParserProvider` carries a class-level `@SuppressWarnings("deprecation")`
(`DataParserProvider.java:20`) to keep that quiet.

**Suggestions are computed once, in a static initialiser.** For the enum-backed
parsers that is fine. It does mean the lists are fixed at class-load time.

## See also

- [WUtils Common](common.md) — module overview and the nullability contract.
- [Core Utilities](utilities.md) — `Args`, the splitter `DustOptionsParser` uses.
- [Config Utilities](config-utils.md) — reading these values out of a config section.
- [WUtils Animation](../animation/animation.md) — the main consumer of parsed particle
  data.
