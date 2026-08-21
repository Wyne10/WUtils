# GUIs

Two configurables extend [`ItemConfigurable`](items.md) to describe a *clickable* item
for an inventory-GUI library. Each targets a different library, and each is behind its
own optional dependency.

| Class | Library | Adds |
|---|---|---|
| `GuiConfigurable` | [triumph-gui](https://github.com/TriumphTeam/triumph-gui) `3.1.13` | slot, and click actions |
| `InvUiItemConfigurable` | [InvUI](https://github.com/NichtStudioCode/InvUI) `1.49` | a structure key character |

Both inherit the entire item vocabulary, so anything on the [Items](items.md) page works
here too.

<!-- allow-code-fences -->

## GuiConfigurable

`configurables/src/main/java/me/wyne/wutils/config/configurables/GuiConfigurable.java`

```yaml
menu:
  closeButton:
    # --- everything from ItemConfigurable ---------------------------------
    material: BARRIER
    name: '<red>Close'
    lore:
      - '<gray>Click to close this menu.'

    # --- GUI additions ----------------------------------------------------
    slot: 26                 # your code reads this; nothing places the item for you

    # Click actions. All three fire together, in this registration order:
    print:                   # message the clicker
      - '<gray>Closing...'
    sound: 'BLOCK_CHEST_CLOSE 1.0 1.0'   # played to the clicker
    command:                 # dispatched AS THE CONSOLE, not the clicker
      - 'say <player> closed the menu'
```

The four added keys are registered in `GuiConfigurable.java:35-41`; they are defined in
`.../configurables/gui/GuiItemAttribute.java:13-18`.

| Key | Type | Effect |
|---|---|---|
| `slot` | int, default 0 | carried only; read it with `getSlot()` (`GuiConfigurable.java:91-93`) |
| `print` | string list | sends each line to `event.getWhoClicked()` |
| `sound` | sound | `event.getWhoClicked().playSound(...)` |
| `command` | string list | `Bukkit.dispatchCommand(consoleSender, ...)` |

`slot` is inert data. Nothing in this module places the item; `getSlot()` exists so your
GUI-building code can.

### Building a GuiItem

`buildGuiItem(ItemAttributeContext)` (`GuiConfigurable.java:59-69`) builds the
`ItemStack` exactly as `ItemConfigurable` does, then wraps it with triumph-gui's
`ItemBuilder.from(stack).asGuiItem(...)`, whose handler runs every attribute implementing
`ClickEventAttribute` (`.../configurables/gui/ClickEventAttribute.java:12-14`). Attributes
implementing `ContextClickEventAttribute` (`.../configurables/gui/ContextClickEventAttribute.java:11-17`)
additionally receive the `ItemAttributeContext`.

The same convenience overloads as `ItemConfigurable` exist —
`buildGuiItem(Player, TextReplacement...)`, `buildGuiItemComponent(...)`
(`GuiConfigurable.java:71-89`).

**The handler does not cancel the event.** Cancelling clicks is triumph-gui's job at the
GUI level, or yours; a `GuiConfigurable` item in an uncancelled GUI can be picked up.

### GuiActionAttribute

`GuiActionAttribute` (`.../configurables/gui/attribute/GuiActionAttribute.java:16-31`)
wraps an arbitrary triumph-gui `GuiAction<InventoryClickEvent>` as an attribute. It is
code-only: `GuiItemAttribute.CLICK` ("click") is defined but deliberately not registered
in `GUI_ITEM_ATTRIBUTE_MAP`, because a lambda cannot come from YAML. Attach one through
an [accessor](attributes.md#accessors):

```java
GuiConfigurable button = base.<GuiConfigurable>getImmutableAccessor()
        .with(new GuiActionAttribute(event -> openSubMenu(event.getWhoClicked())));
```

It extends plain `AttributeBase`, so it never appears in generated config.

### Where the click attributes come from

`print`, `sound` and `command` are separate, minimal reimplementations of what
[`InteractionConfigurable`](interactions.md) already does — a fact the source
acknowledges in a TODO (`.../configurables/gui/attribute/PrintAttribute.java:16`). They
predate the interaction configurable and have not been migrated. Practical differences:

- `command` here always dispatches **as the console**
  (`.../configurables/gui/attribute/CommandAttribute.java:45-50`), with no player-sender
  variant. The interaction vocabulary has both.
- There is no audience selection: output always goes to the clicker.
- `CommandAttribute` also implements `ManualAttribute`
  (`.../configurables/item/ManualAttribute.java:10-18`), so its commands can be fired
  outside a click with `apply(context)` — the only shipped use of that interface.

If you need more than "message, sound, command to the clicker", attach a
`GuiActionAttribute` that calls an `InteractionConfigurable` instead.

## InvUiItemConfigurable

`configurables/src/main/java/me/wyne/wutils/config/configurables/InvUiItemConfigurable.java`

InvUI lays inventories out from a *structure* — a string grid where each character maps
to an ingredient. This configurable adds exactly one key so a config-declared item can
name its character:

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

`getKey()` (`InvUiItemConfigurable.java:46-48`) returns the `char`. The factory takes
`charAt(0)` of the configured string (`.../configurables/invui/attribute/StructureKeyAttribute.java:27-32`),
so a multi-character value silently uses only the first character, and an empty string
throws `StringIndexOutOfBoundsException`.

No click handling is provided — InvUI's own `Item`/`ItemProvider` abstractions cover
that. This class exists purely so the same YAML that describes an item can also say where
it goes in the structure.

## Sharp edges

- **Register custom item attributes before these classes load.** Both static maps copy
  `ItemConfigurable.ITEM_ATTRIBUTE_MAP` in their own static initialisers
  (`GuiConfigurable.java:35-41`, `InvUiItemConfigurable.java:25-28`). A factory added to
  the item map after `GuiConfigurable` is class-loaded never reaches GUI items. Verified:
  the GUI map holds 28 keys to the item map's 24, and the copy happens once.
- **`sound` collides by simple name three ways.** `attribute/common/SoundAttribute`,
  `gui/attribute/SoundAttribute` and `interaction/attribute/SoundAttribute` are three
  classes with the same name; the GUI and interaction ones subclass the common one and
  add a delivery mechanism. Import the right one.
- **`slot` does nothing on its own**, and defaults to `0` — an unconfigured button reads
  back as slot 0, not as "no slot".
- **triumph-gui is pulled in with `net.kyori` excluded** (`configurables/build.gradle:15-17`),
  so it uses whatever Adventure the server provides. That is correct on Paper and will
  break on a platform that does not bundle Adventure.
- Everything on the [Items](items.md) sharp-edges list applies here too.

## See also

- [Items](items.md) — the inherited vocabulary and `build` semantics.
- [Interactions](interactions.md) — the richer alternative to `print`/`sound`/`command`.
- [Attributes and Containers](attributes.md) — accessors, and adding your own keys.
- [Inventories](../common/inventories.md) — `common`'s unrelated low-level
  `InventoryUtils`.
