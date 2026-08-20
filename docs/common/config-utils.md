# Config Utilities

`ConfigUtils` (`common/src/main/java/me/wyne/wutils/common/config/ConfigUtils.java`)
is a single class of static helpers for reading typed, defaultable values out of a
Bukkit `ConfigurationSection`. It layers parsing and fallback-default behavior on top
of Bukkit's raw `getString`/`getStringList`/etc. accessors; it does not read or write
YAML files itself.

**This is not the `config` module.** The separate [`config`](../config/config.md)
module is an annotation-driven system that generates, merges and reloads whole config
files from `@ConfigEntry`-annotated fields. `ConfigUtils` is unrelated, lower-level
plumbing: a plugin can use it directly against any `ConfigurationSection` it already
has, with no annotations or registration involved. The two are easy to confuse by name
only — nothing here depends on or interacts with the `config` module.

Every method is `static` and takes the `ConfigurationSection` to read from explicitly;
there is no shared state. All types are `@NotNull` unless noted otherwise, per the
nullability contract described in the [module overview](common.md).

## Section and path helpers

- **`getConfigurationSection(section, path)`** (`ConfigUtils.java:42-46`) returns the
  nested section at `path`, or `section` itself if `path` does not point to a section.
  Never returns `null` — it degrades to the input section instead.
- **`getPath(section, path)`** (`ConfigUtils.java:53-61`) builds a dotted path for log
  messages by prefixing `path` with `section`'s current path. Returns `path` unchanged
  if `section` is `null` or has no current path (e.g. the root section).

## String and list readers

- **`getStringList(config, path)`** (`ConfigUtils.java:68-72`) reads a string list, but
  treats a single scalar string value at `path` as a one-element list instead of
  returning empty. An unset or blank scalar yields an empty list.
- **`getString(config, path, def)`** (`ConfigUtils.java:78-82`) reads a string, but if
  the value at `path` is a YAML list, joins it into one newline-separated string via
  `reduceString` instead of falling back to `def`. `def` is still used when the key is
  genuinely absent.
- **`reduceString(Collection<String>)`** (`ConfigUtils.java:85-87`) and
  **`reduceString(String, String)`** (`ConfigUtils.java:89-91`) join strings with `\n`.
  The collection overload folds with the two-argument one and returns `""` for an empty
  collection.

## Typed value parsers

These all delegate to the parser for the matching value type, feeding it the raw
string at `path` — see [Ranges](ranges.md), [Durations and Cooldowns](durations.md),
[Comparators and Operations](operations.md), and [Core Utilities](utilities.md)
(`VectorUtils`) for the parsing rules and failure modes of each syntax.

- **`getVector(config, path, def)`** (`ConfigUtils.java:98-100`) parses a
  comma-separated vector (`"1,2,3"`) via `VectorUtils.getVector`. Unlike a typical
  default parameter, a blank *component* falls back to the matching component of
  `def` individually rather than substituting the whole default vector.
- **`getVectorOrZero(config, path)`** (`ConfigUtils.java:103-105`) is `getVector` with
  `VectorUtils.zero()` as the default.
- **`getIntComparator`** / **`getDoubleComparator`** (`ConfigUtils.java:108-115`) parse
  comparator expressions such as `"<5"` or `">=3.0"` via `Comparators`.
- **`getIntOperation`** / **`getDoubleOperation`** (`ConfigUtils.java:118-125`) parse
  operation expressions such as `"+5"` or `"*2.0"` via `Operations`.
- **`getTimeSpan(config, path)`** (`ConfigUtils.java:128-130`) parses a duration
  expression (`"5s"`, `"200ms"`, `"10t"`) via `Durations.getTimeSpan`, unconditionally
  — it does not check for a blank value.
- **`getTimeSpan(config, path, def)`** (`ConfigUtils.java:133-137`) is the same parse,
  but returns `def` without touching `Durations` at all when the string at `path` is
  blank (missing or empty).
- **`getMillis(config, path)`** / **`getTicks(config, path)`**
  (`ConfigUtils.java:140-147`) parse a duration expression and return its length in
  milliseconds or ticks respectively, unconditionally, like the two-argument
  `getTimeSpan`.
- **`getMillis(config, path, def)`** / **`getTicks(config, path, def)`**
  (`ConfigUtils.java:150-161`) add the same blank-returns-`def` short-circuit as the
  three-argument `getTimeSpan`.
- **`getTimeSpanRange`** (`ConfigUtils.java:164-166`), **`getVectorRange`**
  (`ConfigUtils.java:169-171`) and **`getLocationRange`** (`ConfigUtils.java:174-176`)
  parse range expressions (e.g. `"5s..10s"`) via `Durations`, `VectorRange` and
  `LocationRange` respectively. None of the three take a default; all delegate parsing
  failures to the underlying range parser.

The no-default overloads above will propagate whatever exception the underlying
parser throws on malformed or missing input — they do not guard against a blank
string the way their `def`-taking counterparts do.

## Enum readers

- **`getEnumSet(section, key, enumClass)`** (`ConfigUtils.java:183-202`) reads a set of
  enum constants. A boolean value at `key` expands to `EnumSet.allOf` (`true`) or
  `EnumSet.noneOf` (`false`). Otherwise the value is read as a string list (via
  `getStringList`, so a lone scalar works too) and each entry is matched
  case-insensitively against the enum's constant names; an entry that matches nothing
  is **skipped with a logged warning**, not thrown.
- **`getKeyedEnumSet(section, key, enumClass)`** (`ConfigUtils.java:209-245`) is the
  same, but for enums implementing Bukkit's `Keyed`: an entry that doesn't match an
  enum constant name is retried as a `NamespacedKey` against each constant's key. If
  `enumClass` does not implement `Keyed` at all, this delegates straight to
  `getEnumSet`. Both boolean-shorthand and warn-and-skip behavior are unchanged.
- **`getByName(name, enumClass)`** (`ConfigUtils.java:247-256`) matches `name`
  case-insensitively against the enum's constant names. Returns `null` if `name` is
  `null` or matches nothing — it never throws.
- **`getByKeyOrName(key, enumClass)`** (`ConfigUtils.java:258-278`) first tries an
  enum-name match like `getByName`, then, only for `Keyed` enums, retries `key` as a
  `NamespacedKey` against each constant's key. Returns `null` on a `null` input, on no
  match, or if `enumClass` isn't `Keyed` and the name match failed.
- **`getByKeyOrName(section, path, enumClass)`** (`ConfigUtils.java:280-285`) is the
  same lookup, reading the string to match from `section` at `path` first. Returns
  `null` immediately if nothing is set at `path`.

## Material sets and resource extraction

- **`getMaterialEnumSet(section, path)`** (`ConfigUtils.java:288-293`) reads a string
  list and matches each entry to a `Material` via `Material.matchMaterial`. Unlike
  `getEnumSet`, an unmatched entry is **dropped silently** — no warning is logged.
- **`saveDirectoryResource(...)`** has four overloads (`ConfigUtils.java:296-316`),
  all funnelling into the four-argument form
  (`saveDirectoryResource(Plugin, File, boolean)`, `ConfigUtils.java:316-360`). It
  extracts a directory bundled inside the plugin's jar into the plugin's data folder,
  preserving the path relative to the jar root. The shorter overloads default to
  `PluginUtils.getPlugin()` (see [Plugin Composition](plugin.md)) and/or `force=false`.
  The method does nothing if `directory` already exists and `force` is `false`, or if
  `directory` is not located inside the plugin's data folder. It walks the plugin jar's
  entries under the matching prefix, skips any target file that already exists, creates
  parent directories as needed, and copies the rest byte-for-byte. Any `IOException` or
  other failure while opening or reading the jar is logged and swallowed — the method
  never throws.
- **`getRelativePath(file, base)`** (`ConfigUtils.java:366-375`) returns `file`'s path
  relative to `base`, with `/` separators regardless of platform. It canonicalizes both
  paths first; if either canonicalization throws `IOException`, the method **returns
  `null`** rather than propagating the exception. This is the failure mode
  `saveDirectoryResource` relies on to silently no-op when the plugin's jar or data
  folder path can't be resolved.

## See also

- [Ranges](ranges.md), [Durations and Cooldowns](durations.md), [Comparators and
  Operations](operations.md), [Core Utilities](utilities.md) — the parsers these
  methods delegate to.
- [Plugin Composition](plugin.md) — `PluginUtils`, used for the plugin-implicit
  overloads of `saveDirectoryResource`.
- [WUtils Config](../config/config.md) — the separate, annotation-driven config module.
