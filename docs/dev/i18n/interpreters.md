# Interpreters and Validation

The `me.wyne.wutils.i18n.language.interpretation` package turns a raw string looked up
in a [`Language`](languages.md) into either another raw string (with replacements/
placeholders applied) or an Adventure `Component`. The
`me.wyne.wutils.i18n.language.validation` package decides what an interpreter does when
a lookup path is missing; it is documented on this page rather than its own, since a
validator is only meaningful plugged into an interpreter.

## The interpreter contracts

`Interpreter` (`i18n/src/main/java/me/wyne/wutils/i18n/language/interpretation/Interpreter.java:14`)
is the shared base: `getStringValidator()`/`setStringValidator(StringValidator)`.
Interpreters are **mutable and shared** — an `I18n` instance holds exactly one string
interpreter and one component interpreter, so swapping the validator on either changes
every future lookup made through that `I18n`, not just ones made afterward through a
new interpreter instance.

`StringInterpreter` and `ComponentInterpreter`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/interpretation/StringInterpreter.java:20`,
`ComponentInterpreter.java:25`) each expose a **regular grid** of lookup methods rather
than one method per concern. The axes:

| Axis | Values |
|---|---|
| Cardinality | single value vs. `List` |
| Placeholders | plain vs. PlaceholderAPI-expanded (`getPlaceholder*`) |
| Placeholder target | `Player` vs. `OfflinePlayer` (only when placeholder-expanded) |
| Replacements | with vs. without trailing `TextReplacement...` varargs |

`StringInterpreter` has 12 methods from this grid (`getString`/`getStringList` don't
need a placeholder-target axis); `ComponentInterpreter` has the same 12 plus
`toString(Component)`/`fromString(String)` for round-tripping to and from its own text
format. Every method reads a `Language` and a `String path`; `getPlaceholderString(...,
null, path)` still expands PlaceholderAPI's global (non-player) placeholders.

## `BaseInterpreter`: the default, and the base every `ComponentInterpreter` builds on

`BaseInterpreter`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/interpretation/BaseInterpreter.java:26`)
is the sole `StringInterpreter` implementation, and every `ComponentInterpreter` in this
module extends it (see the hierarchy below).

- **Single-value lookups** (`getString`) resolve `path` against `language.getStringMap()`
  — a flat map of every string in the file, keyed by full dotted path. See
  [the two views](languages.md#the-two-views-per-format).
- **List lookups** (`getStringList`) resolve `path` against `language.getStrings()`,
  **then treat every element of the resulting list as a lookup path in its own right**,
  running each one back through the `StringValidator` against `getStringMap()`
  (`BaseInterpreter.java:83-87`). This is not obvious behavior: a list entry is not used
  as literal text. With the default `EmptyValidator`, an element that matches no key
  passes through unchanged, which makes this look like a no-op — but an element that
  *does* match a key name is silently replaced by that key's value instead of being kept
  verbatim. Now that keys are full dotted paths, an entry like `messages.welcome` is a
  live candidate for that substitution.

Every `getPlaceholder*`/`getPlaceholder*List` method delegates to the plain equivalent
and then runs the result through `PlaceholderAPIWrapper.setPlaceholders`.

## Component interpreter implementations

Six concrete classes, from three serializer backends, each with a plain and an `Item*`
variant:

| Class | Serializer backend | Extends | `toString()` format |
|---|---|---|---|
| `LegacyInterpreter` | Adventure `LegacyComponentSerializer` (`&`-codes, hex colors) | `BaseInterpreter` | legacy `&`-codes |
| `ItemLegacyInterpreter` | Same serializer, italic-stripped | `BaseInterpreter` | legacy `&`-codes |
| `EnhancedLegacyInterpreter` | vankka EnhancedLegacyText | **`LegacyInterpreter`** | legacy `&`-codes (inherited — see below) |
| `ItemEnhancedLegacyInterpreter` | EnhancedLegacyText, italic-stripped | **`LegacyInterpreter`** (not `EnhancedLegacyInterpreter`) | legacy `&`-codes (inherited) |
| `MiniMessageInterpreter` | Adventure MiniMessage | `BaseInterpreter` | MiniMessage |
| `ItemMiniMessageInterpreter` | MiniMessage, italic-stripped | `BaseInterpreter` | MiniMessage |

Two things worth being precise about:

- **The class hierarchy is not what the names suggest.** `ItemEnhancedLegacyInterpreter`
  extends `LegacyInterpreter` directly, *not* `EnhancedLegacyInterpreter` — despite the
  name pairing it with the enhanced variant. Both Enhanced classes extend
  `LegacyInterpreter` (`EnhancedLegacyInterpreter.java:26`,
  `ItemEnhancedLegacyInterpreter.java:28`).
- **EnhancedLegacyText has no serializer of its own**, only a parser. Both Enhanced
  classes therefore inherit `toString(Component)` from `LegacyInterpreter` and serialize
  using the plain legacy `&`-code format — a `Component` round-tripped through either
  Enhanced interpreter's `toString`/`fromString` does not preserve enhanced-syntax
  features, only whatever the legacy format can express. `fromString` on both Enhanced
  classes *is* overridden to parse with EnhancedLegacyText, so parsing is enhanced but
  serializing is not.

`EnhancedLegacyInterpreter` and `ItemEnhancedLegacyInterpreter` need the
`enhancedlegacytext` dependency on the consumer's classpath (`compileOnly` here — see
[the overview](i18n.md#dependencies)); `MiniMessageInterpreter` and
`ItemMiniMessageInterpreter` need `adventure-text-minimessage`.

### The `Item*` variants

`ItemLegacyInterpreter`, `ItemEnhancedLegacyInterpreter`, and
`ItemMiniMessageInterpreter` each wrap their result in
`Component.empty().decoration(TextDecoration.ITALIC, false).append(...)`. Vanilla
Minecraft clients render item lore italic by default; this wrapping strips that default
while still letting the source text opt back into italics explicitly (an inner
`<italic>` or `&o` still applies). Use the `Item*` variant for anything destined for
item lore, and the plain variant for chat, action bars, etc.

## Enum factories

`ComponentInterpreters`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/interpretation/ComponentInterpreters.java:13`)
and `StringInterpreters`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/interpretation/StringInterpreters.java:11`)
are small factories: `.get(StringValidator)` constructs an interpreter of that kind.
`ComponentInterpreters` has six constants matching the table above;
`StringInterpreters` has only `BASE`, since `BaseInterpreter` is the only
`StringInterpreter` implementation.

## Validation: what happens when a path is missing

`StringValidator`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/validation/StringValidator.java:16`) is
a `@FunctionalInterface` resolving a path against a `Language`'s flat string map
(`getStringMap()` — see [Languages](languages.md)), deciding the fallback for a missing
path:

| Implementation | Behavior on a missing path |
|---|---|
| `EmptyValidator` (default) | Returns the path itself |
| `ReplaceValidator` | Returns a fixed configured string |
| `ExceptionValidator` | Throws `IllegalArgumentException` |
| `NullValidator` | Returns `null` |

`StringValidator#validateString` is declared `@NotNull`, and interpreters rely on that
holding
(`i18n/src/main/java/me/wyne/wutils/i18n/language/validation/StringValidator.java:30`).
**`EmptyValidator` is the right choice almost always** — returning the missing path
itself makes a gap obvious in-game without breaking anything downstream. Reach for
`ReplaceValidator` when you want a fixed substitute, or `ExceptionValidator` when a
missing string should be a hard, immediate failure rather than something a player
notices first.

`NullValidator` deliberately violates that contract: it returns `null`, which then
escapes through interpreter methods annotated non-null and surfaces later and elsewhere
— deeper in the interpreter, inside a rendered message, or as an immediate crash at a
Kotlin call site that trusted the annotation
(`i18n/src/main/java/me/wyne/wutils/i18n/language/validation/NullValidator.java:31-34`).
It exists for poking at behavior, not for production use.

## See also

- [WUtils Internationalization](i18n.md) — how an interpreter is selected and reached
  through `I18n#accessor`.
- [Languages](languages.md) — the two views these interpreters resolve paths against.
- [Localized Values and Access](localized.md) — the resolved-value wrappers these
  interpreters produce.
- [Replacements](replacements.md) — `TextReplacement`/`ComponentReplacement`, applied
  after the interpreter resolves raw text.
