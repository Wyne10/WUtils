# Configurables

`wutils-configurables` is a library of ready-made [Configuration](../config/config.md)
field types: an item, a GUI button, a message-and-sound interaction, a particle
animation, a numeric range, a cooldown. Declare a field of one of these types, annotate
it with `@ConfigEntry`, and a server owner can describe a whole custom item — material,
name, lore, enchantments, potion effects, attribute modifiers — in YAML, with no code
change on your side.

Reach for it whenever you'd otherwise hand-write a `ConfigurationSection` reader for an
item, a clickable GUI button, a "send this message and play this sound" interaction, or
an animation sequence. Skip it for a lone `int` or `String` setting — that's plain
[Configuration](../config/config.md), no need for this module at all.

## Adding it to your build

```kotlin
dependencies {
    implementation("io.github.wyne10:wutils-configurables:1.21.8")
}
```

`wutils-configurables` pulls in `wutils-common` and `wutils-config` as `api`
dependencies automatically — you don't declare those yourself.

## Third-party and WUtils dependencies you must supply

| Dependency | Needed for | Scope |
|---|---|---|
| Paper API 1.16.5 | almost everything in this module | `compileOnly`, supply at runtime |
| [`wutils-i18n`](../i18n/i18n.md) | **items, interactions, animations and GUIs** — anything with text | `compileOnly`, supply at runtime |
| [`wutils-animation`](../animation/animation.md) | animation configurables only | `compileOnly`, supply at runtime |
| [triumph-gui](https://github.com/TriumphTeam/triumph-gui) `3.1.13` | `GuiConfigurable` only | `compileOnly`, supply at runtime |
| [InvUI](https://github.com/NichtStudioCode/InvUI) `1.49` | `InvUiItemConfigurable` only | `compileOnly`, supply at runtime |

**i18n is the one to watch.** It's declared `compileOnly`, which makes it *look*
optional, but every text-bearing attribute — item names and lore, interaction messages,
titles, GUI click output — resolves its string through `wutils-i18n` at build time, not
load time. Skip adding it and your plugin compiles and starts fine; the first time you
actually build an item or send an interaction with text in it, you get a
`NoClassDefFoundError`. Add `wutils-i18n` alongside this module unless you are
absolutely certain you'll never touch a text-bearing attribute. triumph-gui, InvUI and
`wutils-animation` are genuinely optional — each is confined to its own configurable, and
you only need the one(s) you actually use.

## Two families of configurable

**Value configurables** wrap exactly one parsed value: `IntRangeConfigurable` holds a
range, `TimeSpanConfigurable` holds a duration, `SoundConfigurable` holds a sound. Thin
wrappers, one constructor call, one YAML scalar. See [Value Configurables](values.md).

**Attribute configurables** are open-ended: they hold a *set* of independently parsed
pieces, and which pieces are present is decided entirely by what the YAML contains, not
by the class. Items, interactions, animation steps and GUI buttons are all this kind —
omitting a key just means that piece is never touched. There's no `enabled: false`
anywhere in this vocabulary; absence *is* "leave this alone". That's what lets a single
item description grow two dozen optional features (material, enchantments, potion
effects, attribute modifiers, ...) without you writing a matching number of `if`
branches.

## A quick example

```java
public class ShopConfig {

    @ConfigEntry(section = "shop", path = "sword")
    public static ItemConfigurable sword = new ItemConfigurable();
}
```

```yaml
shop:
  sword:
    material: DIAMOND_SWORD
    name: '<gold>Champion''s Blade'
    lore:
      - '<gray>Forged for the arena.'
```

```java
ItemStack stack = ShopConfig.sword.build(player);
```

`ItemConfigurable` (like every attribute configurable) implements the same
`ConfigSerializable`/`ConfigDeserializable` pair described in [Custom Config
Types](../config/serialization.md) — it just ships a factory for essentially every
common item property so you never implement that pair yourself.

## The map

| Page | Covers |
|---|---|
| [Item Configurables](items.md) | `ItemConfigurable` — material, name, lore, enchantments, potion effects, attribute modifiers, skulls, and more |
| [Interaction Configurables](interactions.md) | `InteractionConfigurable` — audiences, messages, titles, sounds, dispatched commands |
| [Animation Configurables](animations.md) | `AnimationConfigurable`/`AnimationStepConfigurable` — timed sequences of particles, sounds, fireworks, titles |
| [GUI Configurables](guis.md) | `GuiConfigurable` (triumph-gui) and `InvUiItemConfigurable` (InvUI) — clickable items for an inventory menu |
| [Value Configurables](values.md) | ranges, materials, sounds, time spans, comparators, operations, lists and maps |
| [Writing Your Own Configurable](custom.md) | adding a new YAML key to any of the above, or building a configurable of your own |

## Sharp edges

- **The i18n `compileOnly` trap above** is the single most common way to break a plugin
  using this module — worth re-reading if you skimmed the dependency table.
- **Every shipped configurable's vocabulary is a public, mutable registry** — an
  `AttributeMap` such as `ItemConfigurable.ITEM_ATTRIBUTE_MAP`. Registering your own
  factory into one is the supported way to add a config key to every item in your
  plugin; see [Writing Your Own Configurable](custom.md) for how and for the ordering
  traps that come with it.
- **Attribute order is registration order, not YAML order.** If you're relying on one
  attribute reading state another attribute set (for example, an attribute that inspects
  a material's max durability), the order your factories were registered in is what
  decides whether that works — not the order someone wrote the keys in their config.

## See also

- [Configuration](../config/config.md) — the annotation and generation machinery every
  configurable here plugs into.
- [Custom Config Types](../config/serialization.md) — the `toConfig`/`fromConfig`
  contract and `ConfigBuilder`, which every configurable in this module is built on.
- [Internationalization](../i18n/i18n.md) — how the text inside these configs gets
  interpreted (MiniMessage, PlaceholderAPI, translation keys).
