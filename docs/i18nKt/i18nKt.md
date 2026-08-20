# WUtils Internationalization Kotlin

`i18nKt` is a thin Kotlin companion to the [`i18n`](../i18n/i18n.md) module: extension
functions and properties that collapse the Java API's `I18n.global.accessor(x, path)
.getComponent(Placeholder.replace(...))` shape into idiomatic Kotlin. It adds no
behavior of its own — every function delegates straight to `i18n`.

- Directory: `i18nKt/`
- Gradle project: `:WUtils-i18n-kotlin`
- Maven artifact: `io.github.wyne10:wutils-i18n-kotlin`
- Version: inherited from `:WUtils-i18n` (currently `5.6.1`)
- Root package: `me.wyne.wutils.i18n.kotlin`

Source of these facts: `i18nKt/build.gradle.kts`.

The version is not declared independently — `build.gradle.kts` reads it from
`:WUtils-i18n`, and `i18n`'s `publish` task is wired to finalize with this module's, so
the two always ship together at the same version.

## Dependencies

| Dependency | Scope | Notes |
|---|---|---|
| `:WUtils-i18n` | `api` | Re-exported: depending on this module gives you `i18n` too, no separate declaration needed. |
| `org.jetbrains.kotlin:kotlin-stdlib:2.4.10` | `compileOnly` | **Not bundled.** The consuming plugin supplies the stdlib — normal for a Kotlin plugin, but it means this artifact is unusable from a project without one. |
| `com.destroystokyo.paper:paper-api:1.16.5` | `compileOnly` | For the `Player`/`OfflinePlayer`/`CommandSender` receivers. |
| `net.kyori:adventure-text-minimessage:4.26.1` | `compileOnly` | For the serializer properties. |
| `net.kyori:adventure-platform-bukkit:4.4.1` | `compileOnly` | Inherited need; see [Localized Values and Access](../i18n/localized.md). |

The module targets `jvmToolchain(16)`, matching the Java level used across the repo.

## Layout

Two files, no classes — everything is a top-level extension:

| File | Contents |
|---|---|
| `i18nKt/src/main/kotlin/me/wyne/wutils/i18n/kotlin/i18n.kt` | Accessor shorthands, collection helpers, serializer properties |
| `i18nKt/src/main/kotlin/me/wyne/wutils/i18n/kotlin/Replacements.kt` | Infix replacement builders |

From Java these compile to the synthetic classes `I18nKt` and `ReplacementsKt`, but the
module is meant for Kotlin callers; the Java API it wraps is more pleasant to use
directly from Java.

## Accessor shorthands

Four receivers, each with the same eight functions
(`i18nKt/src/main/kotlin/me/wyne/wutils/i18n/kotlin/i18n.kt:17`):

| Function | Returns |
|---|---|
| `localizedString(path, vararg)` | `LocalizedString` |
| `localizedStrings(path, vararg)` | `List<LocalizedString>` |
| `localizedComponent(path, vararg)` | `LocalizedComponent` |
| `localizedComponents(path, vararg)` | `List<LocalizedComponent>` |
| `placeholderString(path, vararg)` | `PlaceholderLocalizedString` |
| `placeholderStrings(path, vararg)` | `List<PlaceholderLocalizedString>` |
| `placeholderComponent(path, vararg)` | `PlaceholderLocalizedComponent` |
| `placeholderComponents(path, vararg)` | `List<PlaceholderLocalizedComponent>` |

The `placeholder*` variants additionally run the result through PlaceholderAPI for the
receiver; the `localized*` variants do not.

Receivers, and what each contributes:

| Receiver | Path comes from | Locale |
|---|---|---|
| `Player?` | the `path` argument | the player's own locale |
| `OfflinePlayer?` | the `path` argument | **always the default language** |
| `CommandSender?` | the `path` argument | the sender's locale if it is a `Player`, otherwise the default |
| `String` | **the receiver itself** | always the default language |

The `String` receiver is the no-audience form: the string *is* the path
(`i18n.kt:89`), and its `placeholder*` functions pass a `null` player
(`i18n.kt:101`), so only PlaceholderAPI's global placeholders expand.

### Two things to know

**An `OfflinePlayer` receiver never selects a per-player language.** These functions
route through `I18n#accessor(Object, String)`, whose locale resolution recognizes only
`Player`. An `OfflinePlayer` — even one who is currently online, obtained as an
`OfflinePlayer` — resolves to the default language. The same applies to a
`CommandSender` that is not a `Player`, which is correct there (the console has no
locale) but easy to miss for `OfflinePlayer`.

**Every function dereferences `I18n.global` with `!!`.** The global is nullable and
starts unset, so calling any of these before assigning `I18n.global` throws
`NullPointerException`. See [the global instance](../i18n/i18n.md#the-global-instance).

## Infix replacements

`Replacements.kt` wraps the `Placeholder`/`ComponentPlaceholder` factories as infix
functions, so a replacement reads as `key infix value`
(`i18nKt/src/main/kotlin/me/wyne/wutils/i18n/kotlin/Replacements.kt:11`):

| Infix | Builds | Equivalent to |
|---|---|---|
| `"key" replace value` | `TextReplacement` | `Placeholder.replace` |
| `"key" plain component` | `TextReplacement` | `Placeholder.plain` |
| `"key" plainText component` | `TextReplacement` | `Placeholder.plainText` |
| `"key" legacy component` | `TextReplacement` | `Placeholder.legacy` |
| `"key" miniMessage component` | `TextReplacement` | `Placeholder.miniMessage` |
| `"regex" regex value` | `TextReplacement` | `Placeholder.regex` |
| `"key" replaceComponent value` | `ComponentReplacement` | `ComponentPlaceholder.replace` |
| `"regex" regexComponent value` | `ComponentReplacement` | `ComponentPlaceholder.regex` |
| `pattern regexComponent value` | `ComponentReplacement` | `ComponentPlaceholder.regex`, `Pattern` receiver |

The receiver is the **placeholder key, not the text being replaced** — `"player"
replace "Wyne"` means "substitute `<player>` with `Wyne`". `replaceComponent` and
`regexComponent` each accept a `String`, a `Component`, or a `BaseComponent` array
(`Replacements.kt:35`).

Chain with `andThen` (`Replacements.kt:62`), which wraps `Replacement#then` and exists
for both replacement kinds.

Which kind to reach for is the same decision as in Java, and it matters: a
`TextReplacement` is substituted before the interpreter parses the string, so its value
can inject markup, while a `ComponentReplacement` is applied afterwards and cannot. See
[the comparison in the `i18n` overview](../i18n/i18n.md#textreplacement-vs-componentreplacement).

## Serializer properties

Symmetric extension properties replace the static `I18n.serializeX`/`deserializeX`
helpers, using the same name on both sides — on a `Component` it serializes
(`i18n.kt:140`), on a `String` it deserializes (`i18n.kt:161`):

`legacy`, `legacySection`, `gson`, `plain`, `plainText`, `miniMessage`. `Component` also
has `bungee` (to a `BaseComponent` array), and `Array<BaseComponent>` has `component`
back the other way.

`CommandSender` gains `player` (a `Player` cast, or `null`) and `locale` (that player's
locale, or `null`) at `i18n.kt:134`.

## Collection helpers

At `i18n.kt:113`: `reduceRaw()` joins a `Collection<String>` or `Collection<Component>`
with newlines; `reduce()` does the same for collections of `LocalizedString` or
`LocalizedComponent`, unwrapping each first. All return `null` for an empty collection,
via `reduceOrNull` — unlike the Java `I18n.reduce*` helpers, which return an empty
string or `Component.empty()`. `asComponents()` maps any `Collection<ComponentLike>`.

`apply(vararg)` (`i18n.kt:128`) applies replacements across a collection — one overload
for `Collection<LocalizedComponent>` with `ComponentReplacement`s, one for
`Collection<String>` with `TextReplacement`s. Note the name collides with Kotlin's
stdlib `apply` scope function; they are distinguished by argument shape, so
`list.apply { ... }` still resolves to the stdlib one, but the similarity is worth
knowing when reading call sites.

<!-- allow-code-fences -->

## Working example

Against a language file containing:

```yaml
greeting: '<gray>Welcome, <gold><player></gold>! You have <count> new messages.'
messages:
  farewell: '<gray>Goodbye, <player>.'
```

the shorthand and infix forms compose like this:

```kotlin
// String receiver: the string is the path, default language
"greeting".localizedString("player" replace "Wyne", "count" replace 3).get()
"messages.farewell".localizedString("player" replace "Wyne").get()

// andThen chains replacements
val both = ("player" replace "Wyne") andThen ("count" replace 7)
"greeting".localizedString(both).get()

// serializer properties, both directions
val c = "greeting".localizedComponent("player" replace "Wyne", "count" replace 1).get()
c.plainText
c.miniMessage
"<red>hi".miniMessage.plainText

// ComponentReplacement, applied after parsing
val fancy = "player" replaceComponent Component.text("Wyne").color(NamedTextColor.AQUA)
"greeting".localizedComponent().replace(fancy).get().miniMessage
```

producing, in order:

```text
<gray>Welcome, <gold>Wyne</gold>! You have 3 new messages.
<gray>Goodbye, Wyne.
<gray>Welcome, <gold>Wyne</gold>! You have 7 new messages.
Welcome, Wyne! You have 1 new messages.
<gray>Welcome, <gold>Wyne</gold>! You have 1 new messages.
hi
<gray>Welcome, <aqua>Wyne</aqua>! You have \<count> new messages.
```

The last line shows an unsubstituted `<count>` coming back escaped from MiniMessage
serialization — harmless in the rendered component, and explained in
[the `i18n` overview](../i18n/i18n.md#textreplacement-vs-componentreplacement).

With a player in hand, the receiver carries both the locale and the PlaceholderAPI
context, which is the form most plugin code uses:

```kotlin
player.placeholderComponent("greeting", "player" replace player.name)
      .sendMessage(player)

player.placeholderStrings("messages.motd", "player" replace player.name)
```

`placeholderComponent` resolves `greeting` in the player's own language, applies the
replacement, then expands PlaceholderAPI placeholders for that player — the Java
equivalent is `I18n.global.accessor(player, "greeting").getPlaceholderComponent(player,
Placeholder.replace("player", player.name))`.

## See also

- [WUtils Internationalization](../i18n/i18n.md) — the module this one wraps: builders,
  the global instance, and the lookup flow.
- [Replacements](../i18n/replacements.md) — the `Placeholder`/`ComponentPlaceholder`
  factories behind the infix functions, including the regex-key hazard that
  `Placeholder` (and therefore `replace`, `regex`) inherits.
- [Localized Values and Access](../i18n/localized.md) — what `LocalizedString` and
  `LocalizedComponent` offer once you have one.
