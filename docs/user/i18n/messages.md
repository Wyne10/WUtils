# Sending Messages

Once [`I18n` is built](i18n.md), sending a localized message to a player is three steps:
get an accessor for the path, apply any replacements, send it. This page walks through
that flow and the choices along the way.

## Getting a string or component for a player

`I18n#accessor(path)` (or `accessor(localeContainer, path)` to resolve against a specific
player's language) gives you a `LocalizationAccessor` for one dotted key. From there,
`getString(...)`/`getComponent(...)` resolve it using the default language, and
`getPlaceholderString(player, ...)`/`getPlaceholderComponent(player, ...)` resolve it
against that player's language and also run it through PlaceholderAPI if present:

```java
LocalizedComponent farewell = I18n.global
        .accessor(player, "messages.farewell")
        .getComponent();

farewell.sendMessage(player);
```

`getComponent()` returns a `LocalizedComponent`, which is `ComponentLike` and knows how
to send itself — you don't need to unwrap it into a raw Adventure `Component` just to
send a chat message.

### Sending to different targets

`LocalizedComponent` has overloads for every common target, each resolved through the
configured `ComponentAudiences`:

```java
farewell.sendMessage(player);          // a specific Player
farewell.sendMessage(commandSender);   // a CommandSender
farewell.sendMessageAll();             // every online player
farewell.sendMessageConsole();         // just the console
farewell.sendActionBar(player);        // action bar instead of chat
farewell.sendMessage("some.permission.node"); // everyone with that permission
```

`sendMessagePlayer(CommandSender)` sends only if the sender turns out to be an actual
`Player`, and quietly does nothing otherwise — handy when you have a `CommandSender` and
don't want to check its type yourself first.

### Getting a plain string instead

If you need a raw string rather than a sendable component — for a log line, a GUI title,
or feeding another API — use the string side of the same accessor:

```java
String message = I18n.global
        .accessor("messages.farewell")
        .getString()
        .get();
```

`getString(...)` returns a `LocalizedString`; `.get()` unwraps it to the actual `String`.

### Lists

A path whose value is a YAML/JSON list (like `messages.motd` from the
[Internationalization](i18n.md) example) resolves the same way, just through the `*List`
methods:

```java
List<LocalizedComponent> motd = I18n.global
        .accessor(player, "messages.motd")
        .getPlaceholderComponentList(player);

motd.forEach(line -> line.sendMessage(player));
```

## Applying replacements

Both raw strings and language files typically contain `<key>` placeholders. Two types of
replacement exist, and reaching for the right one matters for what a substituted value is
allowed to do.

### `TextReplacement` — substitutes before parsing

Use `Placeholder.replace(key, value)` to build one, then pass it to `getString(...)` or
`getComponent(...)`:

```java
TextReplacement who  = Placeholder.replace("player", player.getName());
TextReplacement both = who.then(Placeholder.replace("count", 3));

Component welcome = I18n.global.accessor("greeting").getComponent(both);
```

`.then(...)` chains replacements left to right. A `TextReplacement` is spliced into the
raw string **before the markup interpreter parses it** — so if the substituted value
itself contains markup (`<red>`, `&c`, ...), that markup gets parsed and rendered. That's
useful for trusted, admin-authored text; it's a problem if the value is player-supplied,
since a player-chosen name containing `<red>` would inject real color into your message.

### `ComponentReplacement` — substitutes after parsing

Use `ComponentPlaceholder.replace(key, value)` for a replacement applied to the
already-built `Component` instead:

```java
ComponentReplacement fancy = ComponentPlaceholder.replace(
        "player", Component.text(player.getName(), NamedTextColor.AQUA));

Component welcome = I18n.global.accessor("greeting").getComponent().replace(fancy);
```

Because this runs after parsing, the substituted value can never inject markup — a
malicious `<red>Evil` stays literal text instead of becoming a color code.
`ComponentPlaceholder` also matches its key **literally**, unlike `Placeholder` (see
below).

### Which to reach for

- **Player-supplied text** (chat input, custom names, book contents, anything you didn't
  write yourself) — always `ComponentReplacement`. It can't be used to inject markup.
- **Trusted, hardcoded values** (counts, plugin-controlled names, fixed labels) — either
  works; `TextReplacement` is the more common default and lets you deliberately pass
  markup through if you want to.
- Need to switch between the two kinds? `TextReplacement#asComponentReplacement()` and
  `ComponentReplacement#asTextReplacement()` convert one to the other, but both throw
  `NullPointerException` if `I18n.global` was never assigned — they round-trip through its
  configured interpreter to convert. See [the `I18n.global` gotcha](i18n.md#the-i18nglobal-field-is-not-initialized-for-you).

### A regex hazard with `Placeholder`

`Placeholder.replace(key, value)` builds its substitution as a regular expression
internally, and only the **value** is escaped — the **key** is not. A key made of plain
identifiers (`player`, `count`, `amount`) is always safe. If you ever build a key
dynamically from untrusted input, use `Placeholder.regex(...)` deliberately or switch to
`ComponentPlaceholder`, whose `replace(key, ...)` matches the key literally regardless of
what characters it contains.

## PlaceholderAPI integration

The `getPlaceholder*` family of accessor methods run PlaceholderAPI's `%placeholder%`
expansion on top of your language file's `<key>` replacements:

```java
LocalizedComponent line = I18n.global
        .accessor("messages.balance")
        .getPlaceholderComponent(player);

line.sendMessage(player);
```

This needs `me.clip:placeholderapi` on the runtime classpath — but unlike most of this
module's optional dependencies, it degrades gracefully rather than crashing: if
PlaceholderAPI isn't present, `getPlaceholder*` methods just skip the PAPI expansion step
and behave like their plain equivalents. You don't need a feature flag or a
`Class.forName` check of your own; calling `getPlaceholderComponent` is always safe to
write, whether or not the server has PlaceholderAPI installed.

There's also a `CommandSender`-accepting overload
(`getPlaceholderComponent(CommandSender, ...)`) for code paths where you don't know
whether you have a `Player` yet — it degrades to non-player placeholder expansion when
the sender isn't one.

## See also

- [Internationalization](i18n.md) — building `I18n`, laying out language files, and the
  `I18n.global` initialization gotcha these examples depend on.
- [the contributor wiki](../../dev/i18n/replacements.md) — `Replacement`'s internals and
  the full regex-hazard writeup.
- [the contributor wiki's Localized Values page](../../dev/i18n/localized.md) — every
  `LocalizedComponent` method, and how `ComponentAudiences` resolves targets.
