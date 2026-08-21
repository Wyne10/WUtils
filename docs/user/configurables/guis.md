# GUI Configurables

Two configurables extend [`ItemConfigurable`](items.md) to describe a *clickable*
item for an inventory-GUI library. Both inherit the entire item vocabulary — material,
name, lore, enchantments, everything on the [Items](items.md) page — and add whatever
their target library needs on top. Pick whichever matches the GUI library you already
use; there's no reason to add a second one just for this.

| Class | Library | Adds |
|---|---|---|
| `GuiConfigurable` | [triumph-gui](https://github.com/TriumphTeam/triumph-gui) `3.1.13` | a slot number, and click actions (`print`, `sound`, `command`) |
| `InvUiItemConfigurable` | [InvUI](https://github.com/NichtStudioCode/InvUI) `1.49` | a structure key character |

## What you need

Both classes live in `wutils-configurables` (see [Configurables](configurables.md)
for coordinates) and, like [Items](items.md), need **`wutils-i18n`** on the runtime
classpath for `name`/`lore` even though it's a `compileOnly` dependency of the module.

On top of that:

- `GuiConfigurable` needs `dev.triumphteam:triumph-gui:3.1.13` at runtime. Without it,
  you get a `NoClassDefFoundError` the first time you call `buildGuiItem(...)`.
- `InvUiItemConfigurable` needs `xyz.xenondevs.invui:invui:1.49` at runtime, same
  failure mode.

Both are `compileOnly` dependencies of `wutils-configurables` — you supply and shade
them yourself. triumph-gui is pulled in with its own Adventure (`net.kyori`) classes
excluded, so it rides on whatever Adventure build your server already provides. That's
correct on Paper; it will not work on a server platform that doesn't bundle Adventure.

## GuiConfigurable

```yaml
menu:
  closeButton:
    # --- everything from ItemConfigurable -------------------------------------
    material: BARRIER
    name: '<red>Close'
    lore:
      - '<gray>Click to close this menu.'

    # --- GUI additions -----------------------------------------------------------
    slot: 26                 # carried as data only - see below

    # All three fire together on click, in this order:
    print:                    # message sent to the clicker
      - '<gray>Closing...'
    sound: 'BLOCK_CHEST_CLOSE 1.0 1.0'   # played to the clicker
    command:                  # dispatched AS THE CONSOLE, not the clicker
      - 'say <player> closed the menu'
```

`slot` is inert — nothing in `GuiConfigurable` places the item into a GUI for you.
Call `getSlot()` from your own GUI-building code to know where it goes.

### Building and placing a GUI item

```java
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;

GuiItem item = closeButton.buildGuiItem(player);

Gui gui = Gui.gui()
        .title(Component.text("Menu"))
        .rows(3)
        .create();

gui.setItem(closeButton.getSlot(), item);
gui.open(player);
```

`buildGuiItem(ItemAttributeContext)` (plus the same `TextReplacement`/`Player`/
component overloads as `ItemConfigurable.build`) builds the `ItemStack` exactly like
`ItemConfigurable` does, then wraps it as a triumph-gui `GuiItem` whose click handler
runs `print`, `sound` and `command`.

**The handler never cancels the click event.** If your `Gui` (or your own listener)
doesn't cancel clicks, a player can still pick this item up. Cancelling clicks is
triumph-gui's job at the GUI level, not this configurable's.

### Key reference

| Key | Type | Effect |
|---|---|---|
| `slot` | int, default `0` | carried only — read with `getSlot()` |
| `print` | string list | sent to the clicking player |
| `sound` | sound spec (see [Items](items.md)/[Interactions](interactions.md)) | played to the clicking player |
| `command` | string list | dispatched as the **console**, always — no player-sender variant |

### Attaching custom click behavior

`print`/`sound`/`command` cover the common case. For anything more elaborate — open a
sub-menu, run arbitrary logic — attach a `GuiActionAttribute` in code instead of
YAML; it wraps a plain triumph-gui click lambda and can't be expressed in config:

```java
GuiConfigurable button = base.<GuiConfigurable>getImmutableAccessor()
        .with(new GuiActionAttribute(event -> openSubMenu(event.getWhoClicked())));
```

If you need message/sound/command behavior richer than what's built in — different
audiences, titles, action bars — build an [Interaction](interactions.md) and call
`send(...)` from inside a `GuiActionAttribute` instead of reaching for `print`.

## InvUiItemConfigurable

InvUI lays an inventory out from a *structure*: a string grid where each character
maps to an ingredient. This configurable adds exactly one key so a config-declared
item can name its own character:

```yaml
gui:
  border:
    material: GRAY_STAINED_GLASS_PANE
    name: ' '
    key: '#'        # first character only; defaults to '.'
  confirm:
    material: LIME_WOOL
    name: '<green>Confirm'
    key: 'c'
```

### Building and placing an InvUI item

```java
import xyz.xenondevs.invui.gui.Gui;

Gui gui = Gui.normal()
        .setStructure(
                "# # # # # # # # #",
                "# # # c # # # # #",
                "# # # # # # # # #")
        .addIngredient(border.getKey(), border.build())
        .addIngredient(confirm.getKey(), confirm.build(player))
        .build();
```

`getKey()` returns the configured character. No click handling is provided — that's
InvUI's own `Item`/`ItemProvider` job; this configurable exists purely so the same
YAML that describes an item can also say where it sits in the structure.

### Key reference

| Key | Type | Effect |
|---|---|---|
| `key` | single character, default `'.'` | this item's structure character |

## Sharp edges

- **`slot` and `key` do nothing by themselves.** `slot` defaults to `0`, `key`
  defaults to `'.'` — an item you forgot to configure a placement for still parses
  successfully and reads back a value; it just isn't the value you meant.
- **`key` silently uses only the first character** of whatever string you write, and
  throws on an empty string. Keep it to one character.
- **Register your own custom item attributes before you touch either of these
  classes.** Both copy the item attribute registry into their own registry the
  moment their class is first loaded by the JVM. A custom attribute you register on
  `ItemConfigurable` *after* `GuiConfigurable` has already been used somewhere in
  your plugin never reaches GUI items — register it before either configurable is
  touched, ideally in your plugin's `onEnable`.
- **There are three unrelated classes literally named `SoundAttribute`** across this
  module (plain items, GUI clicks, interactions). Make sure your IDE imports the one
  under the package you're actually working in.
- Everything in [Items](items.md)' sharp-edges list applies here too, since both
  classes inherit the whole item vocabulary.

## See also

- [Items](items.md) — the inherited vocabulary and what `build` does.
- [Interactions](interactions.md) — audiences, titles and richer message delivery
  than `print`/`sound`/`command`.
- [Configurables](configurables.md) — the module overview and dependency table.
- [Writing Your Own Configurable](custom.md) — registering your own attribute keys.
- [the contributor page](../../dev/configurables/guis.md) — internals of the click
  handler and the registry-copy timing, if you hit the registration-order edge case.
