# Core Utilities

Two small classes sit directly in `me.wyne.wutils.common`, outside any sub-package,
because they are used from everywhere: `Args` for splitting configuration strings and
`MapUtils` for transforming maps.

(The package root also holds `Ticks` and `Delegates`. `Ticks` is documented under
[Durations and Cooldowns](durations.md); `Delegates` is vendored — see
[Vendored helper Library](helper.md).)

## `Args` — a quote-aware splitter

`Args` (`Args.java:21`) splits a string into positional arguments, with defaults for
missing positions. It is what turns a config value like `#FF0000:2.0` into parts, and
it is used across the module — [`particles`](particles.md) parses dust options with it,
and [`ranges`](ranges.md) parses its factory strings the same way.

Three delimiter constants are provided (`Args.java:23-25`):

| Constant | Pattern | Splits on |
|---|---|---|
| `COLON_DELIMITER` | `:` | colons |
| `SPACE_DELIMITER` | `\s+` | runs of whitespace |
| `COLON_OR_SPACE_DELIMITER` | `:|\s+` | either — the default |

`new Args(string)` uses the combined delimiter (`Args.java:40-42`); the two-argument
constructor takes any regex (`Args.java:47-49`). The resulting list is immutable.

### Reading arguments

| Method | Missing index |
|---|---|
| `get(int)` (`Args.java:70-72`) | returns `""` |
| `get(int, String)` (`Args.java:78-82`) | returns the supplied default |
| `getNullable(int)` (`Args.java:62-64`) | returns `null` |
| `getArgs()` (`Args.java:54-56`) | the whole immutable list |

Note an asymmetry: the `get` overloads `trim()` the value they return, while
`getNullable` returns the raw element. If you are comparing results from both, they may
differ by surrounding whitespace.

### Quoted spans survive splitting

The interesting part. A double-quoted span is kept as a single argument even when it
contains delimiter characters, so a value like a WorldEdit mask — `"#surface #solid"` —
comes back as one argument rather than two.

The implementation avoids regex escaping problems by masking rather than escaping: each
quoted span is replaced with its index wrapped in NUL characters, which no delimiter
pattern matches, so the placeholder passes through `split()` intact and is substituted
back afterwards. Restoration uses `Matcher.quoteReplacement`, so `$` and `\` inside a
quoted value are not treated as replacement syntax.

Two things follow from the design. Splitting only takes the masking path if a quote is
present at all, so unquoted input behaves exactly like a plain `split`. And the quotes
themselves are consumed — an argument that should literally contain a double quote
cannot be expressed.

## `MapUtils` — map transformation

Three members (`MapUtils.java:13`).

`map(Map, MapFunction)` (`MapUtils.java:18-20`) transforms every entry of a map into a
new entry, producing a `HashMap`. The three-argument overload
(`MapUtils.java:27-36`) takes a `Supplier` for the destination map instead, which is how
you keep ordering — pass `LinkedHashMap::new` and the result preserves the source's
iteration order. Given that the two-argument form is a `HashMap`, **use the supplier
overload whenever the output order matters.**

`MapFunction<K, V, K2, V2>` (`MapUtils.java:47-49`) is the functional interface: one
method taking a `Map.Entry` and returning a new `Map.Entry`. Both key and value types
may change, which is what distinguishes this from a plain `values().stream().map(...)`.

`entry(K, V)` (`MapUtils.java:39-41`) builds an immutable `Map.Entry` via
`AbstractMap.SimpleImmutableEntry` — the return value a `MapFunction` normally
constructs.

Since transformation writes into the destination with `put`, a `MapFunction` that maps
two source keys onto the same destination key silently keeps only the last one. The
result can be smaller than the input.

## See also

- [WUtils Common](common.md) — module overview and the nullability contract.
- [Durations and Cooldowns](durations.md) — the root `Ticks` utility.
- [Particle Data Parsers](particles.md) and [Ranges](ranges.md) — the main consumers of
  `Args`.
- [Vendored helper Library](helper.md) — `Delegates` and `interfaces/Delegate`.
