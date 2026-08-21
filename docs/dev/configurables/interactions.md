# Interactions

An *interaction* is "something that happens to somebody": a message, a title, an action
bar, a sound, a dispatched command — sent to an audience the config chooses. It is the
answer to the pattern where every plugin ends up with a `messages.yml` plus a
`sounds.yml` plus a `broadcast: true` flag, all wired together by hand.

- `configurables/src/main/java/me/wyne/wutils/config/configurables/InteractionConfigurable.java`
- `.../configurables/InteractionListConfigurable.java`
- attributes in `.../configurables/interaction/attribute/`

An interaction section has two halves: **who** (audience attributes) and **what**
(payload attributes). Both are optional; omitting the audience half means "the sender".

<!-- allow-code-fences -->

## A working example

```yaml
levelUp:

  # --- WHO ---------------------------------------------------------------
  # Audience keys are additive: declaring several unions them. Their presence
  # is what counts — see "toAll: false does nothing" below.
  toPlayer: true         # the CommandSender passed to send(); this is the default
  # toAll: true          # every player AND the console
  # toPlayers: true      # every online player
  # toConsole: true      # the console only
  # toPermissions:       # everyone holding any of these permissions
  #   - 'myplugin.staff'
  # toWorlds:            # everyone in these worlds (namespaced keys)
  #   - 'minecraft:overworld'
  # toThatPlayers:       # everyone whose name is in this list
  #   - 'Notch'

  # --- WHAT --------------------------------------------------------------
  # All text goes through i18n: MiniMessage, PlaceholderAPI and translation
  # keys all work, resolved against the placeholder target.
  message:
    - '<green>You reached level <level>!'
    - '<gray>Keep going.'

  action: '<yellow>Level <level>'      # action bar; also a list

  title:                               # section form (safe with spaces)
    title: '<gold>Level Up'
    subtitle: '<gray>Level <level>'
    fadeIn: 10t                        # duration expressions: 10t, 1s, 500ms
    stay: 2s
    fadeOut: 10t

  sound: 'BLOCK_NOTE_BLOCK_PLING 1.0 2.0 MASTER'   # sound volume pitch source

  command:                             # dispatched AS the sender
    - 'spawn'
  console:                             # dispatched as the console
    - 'give %player_name% diamond 1'
```

## The attribute vocabulary

Registered in `InteractionConfigurable.java:33-48`; keys in
`configurables/src/main/java/me/wyne/wutils/config/configurables/interaction/InteractionAttribute.java:10-22`. Registration order is the order audiences
are unioned and payloads are delivered.

### Audience attributes

Each implements `InteractionAudienceAttribute` (`interaction/InteractionAudienceAttribute.java:15-23`),
a single `Audience get(CommandSender)`. Every one resolves through
`I18n.global.getAudiences()`, so the i18n module's audience provider decides what
"players" and "console" actually mean.

| Key | Audience |
|---|---|
| `toPlayer` | `audiences.sender(sender)` — whoever `send()` was called with |
| `toAll` | `audiences.all()` |
| `toConsole` | `audiences.console()` |
| `toPlayers` | `audiences.players()` |
| `toPermissions` | union of `audiences.permission(p)` over a string list |
| `toWorlds` | union of `audiences.world(Key.key(w))` over a string list |
| `toThatPlayers` | `audiences.filter(p -> names.contains(p.getName()))` |

`getAudience` (`InteractionConfigurable.java:89-96`) collects them all, unions them with
`Audience.audience(...)`, and — if none were declared — falls back to a `PlayerAudience`,
i.e. the sender. So the minimal interaction is just `message: 'hi'`.

### Payload attributes

Each implements `ContextInteractionAttribute` (`interaction/ContextInteractionAttribute.java:15-28`),
a single `send(Audience, CommandSender, InteractionAttributeContext)`.

| Key | Effect |
|---|---|
| `message` | string list → `component.sendMessage(audience)` |
| `action` | string list → `audience.sendActionBar` |
| `title` | title/subtitle/times → `audience.showTitle` |
| `sound` | → `audience.playSound` |
| `command` | string list dispatched via `Bukkit.dispatchCommand(sender, …)` |
| `console` | string list dispatched via `Bukkit.dispatchCommand(consoleSender, …)` |

`sound` and `title` are thin subclasses of the shared attributes in
`.../configurables/attribute/common/`, so they accept the same string *and* section forms
and their [alias bodies](attributes.md#two-alias-body-shapes) can name those fields
directly. The rest take their value at their own key, so an alias of them has to repeat
the alias name — `extraMessage: {attributeType: message, extraMessage: [...]}`. Both
shapes work; only the flag audiences, which read nothing at all, are unaffected either
way.

A boss-bar attribute is marked TODO and does not exist (`InteractionConfigurable.java:47`).

### Commands are not audience-aware

`command` and `console` iterate the *config list* and dispatch once per entry — they
ignore the audience entirely (`interaction/attribute/PlayerCommandAttribute.java:35-40`,
`interaction/attribute/ConsoleCommandAttribute.java:34-39`). `command` always runs as the
single `CommandSender` handed to `send()`, never as each member of a `toAll` audience.
If you want "give every online player a diamond", write a console command with a `@a`
selector, not a `toPlayers` audience.

## Sending

```java
interaction.send(sender, textReplacement("level", "12"));
interaction.send(sender, placeholderTarget, replacements...);
interaction.sendComponent(sender, componentReplacements...);
```

`send(CommandSender, InteractionAttributeContext)` (`InteractionConfigurable.java:66-70`)
builds the audience once, then hands it to every payload attribute in turn. The
convenience overloads (`InteractionConfigurable.java:72-87`) construct the context for
you; the ones without an explicit placeholder target derive it with
`I18n.toOfflinePlayer(sender)`.

`InteractionAttributeContext` (`interaction/InteractionAttributeContext.java:18-40`)
carries a nullable `OfflinePlayer` *placeholder target* — the player whose PlaceholderAPI
placeholders and language are used — plus the text and component replacements. Note the
asymmetry: the **sender** decides the language of `I18n.global.accessor(sender, …)`, the
**placeholder target** decides whose placeholders are filled in. They are usually the
same player, and the shorthand overloads make them so.

## Lists of interactions

`InteractionListConfigurable` (`.../configurables/InteractionListConfigurable.java`)
holds several interactions and sends them in order. Its value here is that
`fromConfig` (`InteractionListConfigurable.java:60-84`) accepts **three different YAML
shapes**, so a config key can start life as a plain message and grow:

```yaml
# 1. a bare string  -> one interaction with one message line
noPermission: '<red>You cannot do that.'

# 2. a string list  -> one interaction with several message lines
welcome:
  - '<gold>Welcome!'
  - '<gray>Type /help to get started.'

# 3. a section      -> one interaction per child, full vocabulary available
questComplete:
  tellThem:
    title:
      title: '<gold>Quest complete'
      stay: 3s
    sound: 'ENTITY_PLAYER_LEVELUP 1.0 1.0'
  tellEveryone:
    toPlayers: true
    message: '<gray><player> finished the quest.'
  reward:
    console:
      - 'give <player> diamond 3'
```

The child names in form 3 are arbitrary labels; nothing reads them. Verified: form 1 and
2 each build exactly one interaction carrying only a `message`, and form 3 builds one per
child.

Generated config always uses form 3, with children named `interaction-0`, `interaction-1`
and so on (`InteractionListConfigurable.java:50-57`).

## Sharp edges

- **`toAll: false` still sends to everybody.** The flag audiences — `toPlayer`, `toAll`,
  `toConsole`, `toPlayers` — hardcode `true` and their factories ignore the configured
  value entirely (`interaction/attribute/AllAudience.java:22-44`). Only the key's
  *presence* is read. Verified: a section with `toAll: false` parses to an `AllAudience`
  whose value is `true`. To disable one, delete the key.
- **Overlapping audiences deliver twice.** `Audience.audience(...)` is a plain union with
  no de-duplication, so `toAll` plus `toPlayers` sends every online player two copies of
  every message.
- **Declaring any audience removes the sender.** The `PlayerAudience` fallback only
  applies when the audience set is *empty* (`InteractionConfigurable.java:93-94`). Adding
  `toConsole: true` to an interaction that previously defaulted to the sender means the
  sender stops receiving it. Add `toPlayer: true` alongside.
- **The string form of `title` splits on spaces and colons.** `TitleAttribute`'s
  `fromString` uses the default [`Args`](../common/utilities.md) delimiter, so
  `title: 'Hello there 20 60 20'` parses to title `Hello`, subtitle `there`. Verified.
  Quote the parts — `'"Hello there" "and sub" 20 60 20'` works — or use the section form,
  which is what the example above does.
- **Audience and payload order is registration order, not YAML order.** Verified:
  writing `toWorlds`, `toPermissions`, `toAll` in that order still yields
  `toAll, toPermissions, toWorlds`. It only matters for `command`/`console`, where the
  declared order of the two keys does not control which runs first.
- **`toWorlds` takes namespaced keys, not world names.** It builds `Key.key(value)`
  (`interaction/attribute/WorldAudience.java:34-38`), so a bare `world` is read as
  `minecraft:world`; a name with capitals or underscores throws `InvalidKeyException`.
- **i18n is required at runtime.** Every payload attribute touches `I18n.global`. See the
  [module overview](configurables.md#dependencies).

## See also

- [Attributes and Containers](attributes.md) — the resolution rules behind these keys.
- [Animations](animations.md) — animation steps can embed an interaction or an
  interaction list as a timed effect.
- [WUtils Internationalization](../i18n/i18n.md) — audiences, accessors, and how the text
  is interpreted.
