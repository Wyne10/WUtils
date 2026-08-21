# Item Configurables

`ItemConfigurable` turns a YAML section into a Bukkit `ItemStack`: material, amount,
name and lore, enchantments, attribute modifiers, durability, potion data, skull
textures, armor color. Reach for it whenever you want a server owner to redefine an
item — a kit reward, a shop entry, a quest item — without you touching code. Skip it
for items you build once in code and never expose to config; the attribute machinery
is overhead you don't need there.

Every key is optional, and there is no `enabled: false` anywhere in this vocabulary —
omitting a key just means that aspect of the item is never touched.

## What you need

`ItemConfigurable` lives in `wutils-configurables`. See
[Configurables](configurables.md) for the Gradle coordinates.

`name` and `lore` are resolved through **`wutils-i18n`**. i18n is declared
`compileOnly` by `configurables`, which means it compiles fine without it, but the
first time you call `build(...)` on an item with a `name` or `lore` key, you get a
`NoClassDefFoundError` if `wutils-i18n` isn't actually on the runtime classpath. Add
it alongside `wutils-configurables` even if you never call its API directly. See
[Internationalization](../i18n/i18n.md).

`build(...)` also needs a live server — it calls `ItemStack#getItemMeta()` — so you
can parse config at plugin startup, but you can't build the actual `ItemStack` before
the server is up.

## A complete example

```yaml
sword:

  # --- identity ------------------------------------------------------------
  material: DIAMOND_SWORD    # any Material name; defaults to STONE if omitted
  amount: 1                  # defaults to 1

  # Name and lore go through i18n: MiniMessage tags, PlaceholderAPI placeholders
  # and translation keys all work here.
  name: '<gradient:#ff0000:#ffaa00>Ember Blade</gradient>'
  lore:
    - '<gray>Forged in the deep.'
    - '<gray>Damage: <red><damage></red>'   # <damage> filled in when you build it

  # --- enchantments ----------------------------------------------------------
  # One enchantment: "<key> <level>". The key may be namespaced.
  enchantment: 'minecraft:sharpness 5'

  # Several: a section of arbitrarily-named children, in either form.
  enchantments:
    unbreaking: 'minecraft:unbreaking 3'
    mending:
      enchantment: minecraft:mending
      level: 1

  glow: true                 # fake enchant glint - see "Sharp edges" below

  # --- attribute modifiers ----------------------------------------------------
  # "<attribute> <amount> <operation> <slot>". NOT namespaced.
  attribute: 'generic.attack_damage 10 ADD_NUMBER HAND'

  attributes:
    speed:
      attribute: generic.movement_speed
      amount: -0.05
      operation: ADD_SCALAR
      slot: HAND
      # uuid: optional; a random one is generated per load if omitted

  # --- durability --------------------------------------------------------------
  durability: 1500           # remaining durability (maxDurability - damage)
  unbreakable: true
  repairCost: 3               # anvil prior-work penalty

  # --- presentation --------------------------------------------------------------
  model: 100112               # CustomModelData
  flags:                      # any ItemFlag constant
    - HIDE_ATTRIBUTES
    - HIDE_UNBREAKABLE
```

Potions, leather armor and player heads use the same attribute vocabulary:

```yaml
potion:
  material: SPLASH_POTION
  potionType: STRENGTH        # base potion type
  potionModifier: EXTENDED    # NONE | EXTENDED | UPGRADED
  potionColor: '#8B0000'      # hex, or a red/green/blue section
  potionEffect: 'REGENERATION 200 1 true true true'
  potionEffects:               # ...or several, in either form
    slow:
      type: SLOW
      duration: 100
      amplifier: 2

tunic:
  material: LEATHER_CHESTPLATE
  armorColor:                  # hex '#RRGGBB' or component form
    red: 20
    green: 120
    blue: 200

head:
  material: PLAYER_HEAD
  skull: Notch                 # a known player name (resolved to a UUID)
  # skull64: 'eyJ0ZXh0dXJlcyI6...'   # ...or a base64 texture value
  # skullPlayer: true                # ...or "whoever this item is built for"
```

## Declaring the field

```java
public class Rewards {
    @ConfigEntry(section = "rewards", comment = "Given on level up")
    public ItemConfigurable sword = new ItemConfigurable();
}
```

Register the holder and read it back like any other config field — see
[Configuration](../config/config.md). Once loaded, `sword` holds the parsed
attributes and is ready to build into an `ItemStack`.

## Building the stack

```java
import me.wyne.wutils.i18n.language.replacement.Placeholder;

ItemStack stack = rewards.sword.build(player, Placeholder.replace("damage", "7"));
player.getInventory().addItem(stack);
```

`build(ItemAttributeContext)` and its convenience overloads —
`build(TextReplacement...)`, `build(Player, TextReplacement...)`,
`buildComponent(...)` — all start from a plain `STONE` stack and apply every
attribute the section declared. The `Player` overloads matter for `name`/`lore`: the
player decides which language is used and whose PlaceholderAPI placeholders are
filled in. `Placeholder.replace(key, value)` builds a `TextReplacement` that
substitutes a `<key>` marker — that's what fills in `<damage>` in the lore above.

## Key reference

| Key | Value | Applies to |
|---|---|---|
| `material` | `Material` name; defaults to `STONE` if absent | the stack's type |
| `amount` | int, default `1` | stack size |
| `name` | i18n string | display name |
| `lore` | i18n string list | lore lines |
| `flags` | `ItemFlag` name list | item flags |
| `skull` | player name → `OfflinePlayer` | `SkullMeta` owner |
| `skull64` | base64 texture value | `SkullMeta` profile |
| `skullPlayer` | boolean | `SkullMeta` owner = whoever you build for |
| `unbreakable` | boolean | unbreakable flag |
| `enchantment` | `<key> <level>`, or a section | one enchantment |
| `enchantments` | section of the above, arbitrary child names | many enchantments |
| `attribute` | `<attr> <amount> <op> <slot>`, or a section | one attribute modifier |
| `attributes` | section of the above | many modifiers |
| `glow` | boolean | fake glint |
| `durability` | int | *remaining* durability |
| `damage` | int | raw damage value (alternative to `durability`) |
| `model` | int | `CustomModelData` |
| `repairCost` | int | anvil repair cost |
| `potionColor` | hex or RGB section | potion color |
| `potionType` | `PotionType` name, default `WATER` | base potion data |
| `potionModifier` | `NONE` / `EXTENDED` / `UPGRADED` | base potion data |
| `potionEffect` | `<type> <duration> <amplifier> <ambient> <particles> <icon>`, or a section | one custom effect |
| `potionEffects` | section of the above | many custom effects |
| `armorColor` | hex or RGB section | leather armor color |

Every attribute that needs a specific item type checks first and does nothing
otherwise — `skull` on a sword, `armorColor` on a sword, `potionType` on a sword are
all silently ignored rather than throwing. That's what makes it safe to reuse one
vocabulary for every material, but it also means the one attribute you actually care
about can be silently skipped if you set it on the wrong material.

## Sharp edges

- **A typo'd material quietly gives you an item missing half its config.** A bad
  `material` value fails the whole load (see below), but a *valid* material that
  isn't what you meant — say `LEATHER_CHESTPLATE` instead of `LEATHER_HELMET` — means
  every attribute for the meta type you intended (`armorColor` still works, both are
  leather) silently applies to the wrong slot, and there's no error telling you.
  Double-check the material line first when an item "isn't picking up" a key.
- **A bad material, enchantment, attribute, potion type, potion modifier, or skull
  name aborts loading the whole config**, not just that one item — it throws with a
  message naming the bad value and its config path (e.g. `Invalid material
  'NOT_A_BLOCK' at rewards.sword.material`). Fix the key it names.
- **`skull` needs a name the server has actually seen** — resolving an unknown name
  throws. Prefer `skull64` for a fixed texture you don't want to depend on a real
  player existing.
- **Two items from the same config carry different attribute-modifier UUIDs** unless
  you set `uuid` explicitly on each modifier in the `attributes` section — a fresh
  random one is generated every time the config is parsed. Two built copies won't
  stack, and the client won't treat their modifiers as equal.
- **`glow` does nothing if the item already has a real enchantment** — it fakes the
  glint by attaching a hidden enchantment, and skips itself when `meta.hasEnchants()`
  is already true. The `sword` example above has `glow: true` *and* real
  enchantments, so `glow` has no visible effect there; it only matters on an
  otherwise-unenchanted item.
- **`glow` hides your real enchantment lore too.** It works by adding a hidden
  enchantment and then flagging `HIDE_ENCHANTS`, and that flag hides *all*
  enchantment lines, including ones you configured on purpose. Don't combine `glow`
  with enchantments you want visible.
- **`lore: []` does not clear existing lore** — an empty list is treated as "nothing
  configured" and skipped rather than as "set the lore to empty."
- **`attribute` and `enchantment` disagree about namespacing.** `enchantment` splits
  only on whitespace, so `minecraft:sharpness 5` works. `attribute` also splits on
  colons, so `minecraft:generic.attack_damage 10 ADD_NUMBER HAND` breaks — use the
  unqualified name (`generic.attack_damage`) in the string form, or use the section
  form if you need to be explicit.
- **The generated config doesn't round-trip your exact formatting.** If your config
  gets regenerated, a section-form `enchantments` entry comes back as a compact
  string (`'minecraft:mending 1'`), and `armorColor`/`potionColor` written as
  `red`/`green`/`blue` comes back as `'#RRGGBB'`. Both still parse to the same value —
  only the file text changes.

## See also

- [GUI Configurables](guis.md) — `GuiConfigurable` and the InvUI item configurable
  both build on everything here.
- [Configurables](configurables.md) — the module overview, dependencies and the
  attribute framework these keys are built from.
- [Writing Your Own Configurable](custom.md) — adding your own item attribute keys.
- [Internationalization](../i18n/i18n.md) — what `name` and `lore` are resolved
  through.
- [the full attribute reference](../../dev/configurables/items.md) — application
  order and the container internals, if you need to go deeper.
