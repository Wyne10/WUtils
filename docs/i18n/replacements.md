# Replacements

The `me.wyne.wutils.i18n.language.replacement` package substitutes values into
localized text — either raw interpreter-formatted strings before they're parsed into a
`Component`, or directly into an already-built `Component` tree.

## `Replacement<T>`

`Replacement<T>`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/replacement/Replacement.java:7`) is
the shared `@FunctionalInterface`: `T replace(T obj)`. `TextReplacement` and
`ComponentReplacement` are its two specializations.

## `TextReplacement` and `ComponentReplacement`

`TextReplacement`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/replacement/TextReplacement.java:7`)
operates on raw strings, as applied by `I18n.applyTextReplacements` (chains every
replacement in order — see [the overview](i18n.md)). `ComponentReplacement`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/replacement/ComponentReplacement.java:8`)
does the same over `Component` trees, via `I18n.applyComponentReplacements`. Both
interfaces offer:

- **`then(...)`** — composes two replacements of the same kind into one that applies
  both in order.
- **A cross-kind adapter** — `TextReplacement#asComponentReplacement()` and
  `ComponentReplacement#asTextReplacement()` each round-trip through
  [`I18n.global`](i18n.md#the-global-instance)'s configured component interpreter
  (`toString`/`fromString`) to convert between the two. Both **throw
  `NullPointerException` if `I18n.global` was never assigned** — this is one of the
  handful of places `I18n.global` is dereferenced unconditionally.

## `Placeholder`: substituting into raw strings — and a regex hazard

`Placeholder`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/replacement/Placeholder.java:25`)
builds `TextReplacement`s that substitute a `<key>` marker in a raw string, with
overloads for a generic `T` (via `String.valueOf`), a plain `String`, a `Component`
(serialized with `I18n.global`'s component interpreter, an explicitly-passed
`ComponentInterpreter`, or one of the named serializers `plain`/`plainText`/`legacy`/
`miniMessage`), and a raw `regex(...)` variant.

**The key is interpolated into a regular expression, unguarded.** Every overload calls
`string.replaceAll("<" + key + ">", Matcher.quoteReplacement(value))` — the
*replacement value* is protected with `Matcher.quoteReplacement`, but `key` is spliced
directly into the pattern without `Pattern.quote`. A key containing regex
metacharacters (`.`, `*`, `(`, `)`, `[`, `]`, ...) is interpreted as part of the pattern
rather than matched literally, which can silently match more (or less, or throw a
`PatternSyntaxException`) than intended. Keys drawn from a fixed, hand-written set of
simple identifiers (`player`, `amount`, ...) are safe in practice; a key built from
untrusted or dynamic input is not.

`ComponentPlaceholder`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/replacement/ComponentPlaceholder.java:16`)
is the `ComponentReplacement` equivalent and is **not** affected by this: its
`replace(String key, ...)` overloads use Adventure's
`TextReplacementConfig.Builder#matchLiteral`, which matches `key`'s characters
literally regardless of regex metacharacters. Its own `regex(...)` overloads (taking a
`@RegExp String` or a `Pattern` explicitly) are intentionally pattern-based and use
`match(...)` accordingly — that distinction is the caller's explicit choice there, not
an accidental one.

## See also

- [WUtils Internationalization](i18n.md) — `I18n.global`, dereferenced by the
  cross-kind adapters above.
- [Interpreters and Validation](interpreters.md) — the interpreters
  `Placeholder`/`ComponentReplacement` round-trip strings through.
- [Localized Values and Access](localized.md) — `LocalizedString#replace` and
  `LocalizedComponent#replace`, the usual call sites for these replacements.
