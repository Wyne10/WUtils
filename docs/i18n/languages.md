# Languages

The `me.wyne.wutils.i18n.language` package defines a single loaded language
(`Language`) and the path-resolution view over it (`LanguageStrings`), plus three
concrete formats: YAML, JSON, and a flat `.lang` file. See
[WUtils Internationalization](i18n.md) for how a `Language` fits into a lookup.

## `Language` and `LanguageStrings`

`Language` (`i18n/src/main/java/me/wyne/wutils/i18n/language/Language.java:13`) is the
interface every format implements. It exposes:

- `getLanguageCode()` / `getLocale()` — the code is the source filename minus extension;
  the locale is `new Locale(languageCode)`, so an unusual code (anything not a real IETF
  language tag) still constructs a `Locale`, just not a meaningful one.
- `getLanguageFile()` — the backing `File`.
- `getStrings()` — a `LanguageStrings` view.
- `getStringMap()` — a flat `Map<String, String>` of every string in the file, keyed by
  its full dotted path.

`LanguageStrings` (`i18n/src/main/java/me/wyne/wutils/i18n/language/LanguageStrings.java:12`)
resolves a path within the same source: `contains(path)`, `isList(path)`, and
`getStringList(path)`.

## The two views, per format

`getStringMap()` and `getStrings()` both resolve dotted nested paths. They differ in
what they carry, not in how deep they reach: the map holds scalar strings, lists are
reached through the strings view.

| Format | `getStringMap()` (single-value lookups) | `getStrings()` (list lookups) |
|---|---|---|
| YAML (`YamlLanguage`) | `section.getKeys(true)` filtered to `section::isString` — every string key at any depth, keyed by full path | `ConfigurationSection#contains`/`getStringList` — resolves dotted nested paths |
| JSON (`JsonLanguage`) | The object tree flattened recursively to dotted paths, keeping string values | Splits the path on `.` and descends through nested objects |
| `.lang` (`LangLanguage`) | The entire loaded flat map | Wraps the *same* map — the format has no nesting |

A nested key `messages.welcome` is therefore reachable as a single string and, if it
holds a list, through `getStringList` — with no difference between the YAML, JSON and
`.lang` backends.

## `YamlLanguage`

`YamlLanguage` (`i18n/src/main/java/me/wyne/wutils/i18n/language/YamlLanguage.java:25`)
reads the file with Bukkit's `YamlConfiguration`. `getStrings()` is backed by
`YamlLanguageStrings`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/YamlLanguageStrings.java:9`), a thin
record wrapping a `ConfigurationSection`.

## `JsonLanguage`

`JsonLanguage` (`i18n/src/main/java/me/wyne/wutils/i18n/language/JsonLanguage.java:34`)
parses the file as a single JSON object with Gson (transitively available via
paper-api — see [the overview](i18n.md#dependencies)). A missing, empty, or unparsable
file loads as an empty object rather than throwing; a file whose root is not a JSON
object logs an error and also falls back to an empty object. `getStrings()` is backed by
`JsonLanguageStrings`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/JsonLanguageStrings.java:12`), which
resolves a dotted path by descending through nested `JsonObject`s one segment at a time.

## `LangLanguage`

`LangLanguage` (`i18n/src/main/java/me/wyne/wutils/i18n/language/LangLanguage.java:29`)
reads a flat `key=value` file, one entry per line: blank lines and lines starting with
`#` are skipped, and everything before the first `=` on a line is the key. There is no
nesting concept in this format — `LangLanguageStrings#isList` always returns `false`,
and `getStringList` returns a **single-element list** wrapping the value for a present
key rather than resolving an actual list
(`i18n/src/main/java/me/wyne/wutils/i18n/language/LangLanguageStrings.java:14`). Because
both of `Language`'s views are backed by the same underlying map here, this is the one
format with no nesting to resolve in the first place.

## `mergeDefaultStrings`: a disk-writing side effect at construction time

Every format's constructor accepting a `defaultLanguage` argument
(`YamlLanguage(Language, File, Logger)`, and the equivalents on `JsonLanguage` and
`LangLanguage`) back-fills keys present in the default language's file but missing from
this one — **and writes the result back to `languageFile` on disk** as part of
construction:

| Format | Merge mechanism |
|---|---|
| YAML | `YamlUpdater.create(languageFile, defaultLanguage.getLanguageFile()).backup(false).update()` — the bundled `ru.vyarus:yaml-config-updater` |
| JSON | A hand-written recursive merge (`JsonLanguage#mergeMissing`) that copies entries present in the default but absent here, descending into nested objects that exist on both sides, then rewrites the whole file with Gson if anything changed |
| `.lang` | Collects missing keys into a map and **appends** them to the end of `languageFile` (adding a blank-line separator first if the file is non-empty), rather than rewriting the whole file |

All three skip the merge entirely — no read, no write — when `defaultLanguage` is `null`
or its backing file is empty (`length() == 0`). Constructing a `Language` with a default
is therefore not a pure read: a plugin author who expects `new YamlLanguage(...)` to
just parse a file should know it can also mutate that file on disk first. This is how
`BaseI18nBuilder.loadLanguage(Plugin, String)` and `PluginI18nBuilder` keep an
already-installed language file in sync with newer keys shipped in a plugin update — see
[the overview](i18n.md#entry-points-i18n-and-its-builders).

## See also

- [WUtils Internationalization](i18n.md) — how `I18n` resolves a `Language` for a
  player and caches lookups against it.
- [Interpreters and Validation](interpreters.md) — what happens when a lookup path
  (resolved through the views described here) is missing.
- [WUtils Config](../config/config.md) — the same `yaml-config-updater` dependency and
  logger-quieting pattern used here for `YamlLanguage`.
