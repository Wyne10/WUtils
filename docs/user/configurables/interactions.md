# Interaction Configurables

An *interaction* is "something that happens to somebody": a message, a title, an
action bar, a sound, a dispatched command, sent to an audience the config chooses.
It replaces the pattern where every plugin ends up with its own `messages.yml` plus a
`sounds.yml` plus a hand-wired `broadcast: true` flag — one YAML section covers who
gets told and what they're told, and you call one method to fire it.

An interaction section has two halves: **who** (audience keys) and **what** (payload
keys). Both are optional — omitting the audience half just means "the sender".

## What you need

`InteractionConfigurable` lives in `wutils-configurables` (see
[Configurables](configurables.md) for coordinates). Every payload key resolves its
text through **`wutils-i18n`** — that dependency is `compileOnly` on the module's
side, so it compiles without it, but you get a `NoClassDefFoundError` the first time
you call `send(...)` if `wutils-i18n` isn't actually on your runtime classpath. See
[Sending Messages](../i18n/messages.md) for how `message`/`action`/`title` text is
actually formatted.

## A complete example

```yaml
levelUp:

  # --- WHO ---------------------------------------------------------------------
  # Audience keys are additive - declaring several unions them together.
  toPlayer: true          # the CommandSender you called send() with; the default
  # toAll: true            # every player AND the console
  # toPlayers: true        # every online player
  # toConsole: true        # the console only
  # toPermissions:         # everyone holding any of these permissions
  #   - 'myplugin.staff'
  # toWorlds:              # everyone in these worlds (namespaced keys)
  #   - 'minecraft:overworld'
  # toThatPlayers:         # everyone whose name is in this list
  #   - 'Notch'

  # --- WHAT --------------------------------------------------------------------
  # All text goes through i18n: MiniMessage tags, PlaceholderAPI placeholders
  # and translation keys all work, resolved against the placeholder target.
  message:
    - '<green>You reached level <level>!'
    - '<gray>Keep going.'

  action: '<yellow>Level <level>'      # action bar; also accepts a list

  title:                                # section form - safe with spaces
    title: '<gold>Level Up'
    subtitle: '<gray>Level <level>'
    fadeIn: 10t                         # duration expressions: 10t, 1s, 500ms
    stay: 2s
    fadeOut: 10t

  sound: 'BLOCK_NOTE_BLOCK_PLING 1.0 2.0 MASTER'   # sound volume pitch source

  command:                              # dispatched AS the sender
    - 'spawn'
  console:                              # dispatched as the console
    - 'give %player_name% diamond 1'
```

## Declaring and sending

```java
public class Messages {
    @ConfigEntry(section = "messages")
    public InteractionConfigurable levelUp = new InteractionConfigurable();
}
```

```java
import me.wyne.wutils.i18n.language.replacement.Placeholder;

messages.levelUp.send(player, Placeholder.replace("level", String.valueOf(newLevel)));
```

`send(CommandSender, TextReplacement...)` resolves the placeholder target from the
sender for you and fires every configured payload at the configured audience.
`send(sender, placeholderTarget, replacements...)` lets you send *as* one sender
(deciding the language) while filling placeholders *for* a different player — the
two are usually the same, but the interaction doesn't require it. `sendComponent(...)`
is the equivalent for pre-built `Component` replacements.

## The attribute vocabulary

### Audiences

| Key | Sends to |
|---|---|
| `toPlayer` | whoever you called `send()` with |
| `toAll` | every player and the console |
| `toConsole` | the console only |
| `toPlayers` | every online player |
| `toPermissions` | everyone holding any permission in the list |
| `toWorlds` | everyone in any world in the list (namespaced keys — see below) |
| `toThatPlayers` | everyone whose name is in the list |

Declaring several unions them, with no de-duplication. If you declare no audience key
at all, the interaction falls back to the sender.

### Payload

| Key | Effect |
|---|---|
| `message` | string list, sent as a chat message |
| `action` | string (or list), sent to the action bar |
| `title` | section (`title`/`subtitle`/`fadeIn`/`stay`/`fadeOut`), shown as a title |
| `sound` | sound spec, played to the audience |
| `command` | string list, dispatched once per entry as the **sender** |
| `console` | string list, dispatched once per entry as the **console** |

`sound` and `title` accept both a compact string form and the section form shown
above. There is no boss-bar payload yet.

### Grammars

- **Sound**: `"<sound> <volume> <pitch> <source>"` — volume, pitch and source are all
  optional and default to `1.0`, `1.0`, `MASTER`. Example:
  `'ENTITY_PLAYER_LEVELUP 1.0 1.0'`.
- **Title (string form)**: splits on **both spaces and colons** — see the sharp edge
  below. Prefer the section form for anything with punctuation or spaces in it.
- **Duration expressions** (`fadeIn`/`stay`/`fadeOut`): `20` (ticks), `20t`, `1s`,
  `1500ms`, or a combination like `1m30s`.

## Lists of interactions

`InteractionListConfigurable` holds several interactions and sends them all, in
order. Its `fromConfig` accepts three shapes, so a config key can start life simple
and grow without you changing its type:

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

The child names in form 3 (`tellThem`, `tellEveryone`, `reward`) are arbitrary labels
— nothing reads them, use whatever's descriptive.

## Sharp edges

- **`toAll: false` still sends to everybody.** Every audience flag key only checks
  whether it's *present*, not what value you gave it — `toAll: false` parses exactly
  like `toAll: true`. To turn an audience off, delete the key rather than setting it
  false.
- **Overlapping audiences deliver duplicate messages.** `toAll` plus `toPlayers`
  sends every online player two copies of everything, since audiences are unioned
  with no de-duplication.
- **Declaring any audience removes the sender.** The "fall back to the sender" rule
  only kicks in when *no* audience key is present at all. If an interaction used to
  rely on that default and you add, say, `toConsole: true` to also log it to console,
  the player who triggered it stops receiving anything — add `toPlayer: true`
  alongside whatever else you add.
- **`command`/`console` ignore the audience entirely.** They dispatch once per list
  entry as a fixed sender (the original sender, or the console) — they do not run
  once per member of `toAll`/`toPlayers`. If you want "give every online player a
  diamond", write a console command with a `@a` selector rather than expecting
  `toPlayers` to fan a `command` out.
- **The string form of `title` splits on spaces *and* colons.** `title: 'Hello there
  20 60 20'` parses to title `Hello`, subtitle `there` — not what you probably meant.
  Quote multi-word parts (`'"Hello there" "and sub" 20 60 20'`) or just use the
  section form, which is what the example on this page does.
- **`toWorlds` takes namespaced keys, not plain world names.** `world` is read as
  `minecraft:world`; a name with capitals or underscores throws. Stick to lowercase,
  namespaced world names.
- **Audience and payload order in your YAML doesn't control delivery order.** Both
  run in a fixed internal order regardless of how you arrange the keys in the file —
  this only matters if you're relying on `command` running before or after `console`.

## See also

- [Item Configurables](items.md) and [GUI Configurables](guis.md) — GUI click
  actions reimplement a small subset of this vocabulary; wrap an
  `InteractionConfigurable` in a `GuiActionAttribute` if you need more than they
  offer.
- [Animation Configurables](animations.md) — animation steps can embed a whole
  interaction (or interaction list) as a timed effect.
- [Internationalization](../i18n/i18n.md) — audiences, accessors, and how the text is
  interpreted.
- [Configurables](configurables.md) — the module overview and dependency table.
- [the contributor page](../../dev/configurables/interactions.md) — attribute
  resolution order and the full `InteractionAttributeContext` contract.
