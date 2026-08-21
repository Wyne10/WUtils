# Localized Values and Access

The `me.wyne.wutils.i18n.language.component` package wraps a resolved string or
`Component` together with where it came from, and resolves Bukkit senders into
Adventure `Audience`s. The `me.wyne.wutils.i18n.language.access` package is documented
on the same page: a `LocalizationAccessor` exists only to produce the `Localized*`
values this page describes, for a fixed `(language, path)` pair.

## `Localized<T, I>` and `BaseLocalized`

`Localized<T, I extends Interpreter>`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/component/Localized.java:11`) is a
resolved value (`T`) plus the [`Interpreter`](interpreters.md) (`I`) that produced it,
the [`Language`](languages.md) it came from, and the lookup path.

`BaseLocalized<T, I>`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/component/BaseLocalized.java:16`) is
the shared implementation. It stores the interpreter as the raw `Interpreter` type and
casts it unchecked in `getInterpreter()` — constructing an instance with an interpreter
that doesn't actually match `I` doesn't fail at construction, only later, as a
`ClassCastException` at whatever call site invokes `getInterpreter()`.

## `LocalizedString` and `LocalizedComponent`

`LocalizedString`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/component/LocalizedString.java:12`)
wraps a resolved string. `replace(TextReplacement...)` returns a **new** instance with
the replacements applied — the original is untouched.

`LocalizedComponent`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/component/LocalizedComponent.java:26`)
wraps a resolved `Component` and additionally implements `ComponentLike`. Beyond
`replace(ComponentReplacement...)` (same new-instance semantics), it offers:

- A family of `sendMessage(...)` overloads accepting an `Audience`, a `Player`, a
  `CommandSender`, a player `UUID`, a permission (`Key` or `String`), a
  `Predicate<CommandSender>` filter, or none of these (`sendMessageAll`,
  `sendMessagePlayers`, `sendMessageConsole`) — each resolving its target through this
  instance's [`ComponentAudiences`](#componentaudiences) (below). `sendMessagePlayer(
  CommandSender)` sends only if the sender is actually a `Player`, silently doing
  nothing otherwise. `sendActionBar(Player)` sends to the action bar instead of chat.
- Serialization convenience methods (`legacy()`, `legacySection()`, `gson()`, `plain()`,
  `plainText()`, `miniMessage()`, `bungee()`, `minecraft()`) — each a thin wrapper over
  the corresponding `I18n.serialize*` static method.
- `styleMap(String key)` / `style(String key, String value)` — see `I18n.styleMap`/
  `I18n.style` on [the overview](i18n.md), which build a map of this component rendered
  in every serializer format, keyed by `key` and `key`-suffixed variants.

## Placeholder variants

`PlaceholderLocalized<T, I>`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/component/PlaceholderLocalized.java:12`)
extends `Localized` with the (possibly `null`, possibly offline) player placeholders
were expanded for: `getOfflinePlayer()` and `getPlayer()` (the latter `null` unless that
player is currently online).

`PlaceholderLocalizedString` and `PlaceholderLocalizedComponent` are the corresponding
implementations, each adding the stored `OfflinePlayer` and overriding `replace(...)` to
carry it through to the new instance they return.

## `ComponentAudiences`

`ComponentAudiences`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/component/ComponentAudiences.java:18`)
resolves Bukkit senders and groups of senders (a single player, a `CommandSender`, a
player by `UUID`, everyone, a permission-filtered group, the console, a named server, a
world) into Adventure `Audience`s. Every method returns a non-null `Audience`.

Two implementations ship, differing in what the server must provide:

| Implementation | Requires | Notes |
|---|---|---|
| `PaperComponentAudiences` (default) | Nothing beyond Paper or a Paper fork | Bukkit types are used as audiences directly, since `Player` and the console sender natively implement `Audience` on Paper |
| `BukkitComponentAudiences` | `adventure-platform-bukkit`, `compileOnly` here | Routes through the `BukkitAudiences` bridge; use it on servers without native Adventure support (pre-Paper Spigot, or older Paper builds) |

Lookups that can find nothing — `player(UUID)` for an offline player, `world(Key)` for
an unloaded world — return an empty audience rather than failing, in both
implementations (`PaperComponentAudiences.java:38-41`, `:81-84`). Sending to the result
is a no-op, so callers do not need to guard these calls.

## `LocalizationAccessor`

`LocalizationAccessor`
(`i18n/src/main/java/me/wyne/wutils/i18n/language/access/LocalizationAccessor.java:26`)
is a fixed `(language, path)` pair with methods to resolve it, on demand and without
caching, into every `Localized*` shape: `getString`/`getComponent` and their
`getPlaceholder*`/`*List` counterparts, mirroring the interpreter method grid described
on [Interpreters and Validation](interpreters.md). `I18n#accessor(...)` chooses between
the two implementations below **once** per `(language, path)` pair and caches the
result — see [accessor caching](i18n.md#accessor-caching) on the overview page.

| Implementation | For a path whose value is | Single-value methods | `*List` methods |
|---|---|---|---|
| `StringLocalizationAccessor` | a single string/component | Resolve directly | Return a **singleton list** wrapping the single-value result |
| `ListLocalizationAccessor` | a list | **Reduce** the list result via `I18n.reduceString`/`reduceComponent` (newline-joined) | Resolve the actual list |

Both are records implementing `LocalizationAccessor`, and both offer `withLanguage
(Language)` to get an equivalent accessor for the same path against a different
language (used internally rather than re-resolving `isList` from scratch).

## See also

- [WUtils Internationalization](i18n.md) — `I18n#accessor` and how it picks between
  `StringLocalizationAccessor` and `ListLocalizationAccessor`.
- [Interpreters and Validation](interpreters.md) — the interpreters these accessors and
  `Localized*` values delegate to.
- [Replacements](replacements.md) — `TextReplacement`/`ComponentReplacement`, accepted
  by most of the methods on this page.
