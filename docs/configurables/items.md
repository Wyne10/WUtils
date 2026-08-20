# Items

`ItemConfigurable` turns a YAML section into an `ItemStack`. It is the most-used
configurable in the module and the base class of both GUI item types.

- `configurables/src/main/java/me/wyne/wutils/config/configurables/ItemConfigurable.java`
- attributes in `.../configurables/item/attribute/`, interfaces in `.../configurables/item/`

<!-- allow-code-fences -->

## A working example

```yaml
# Every key below is optional. Omit one and that aspect of the item is simply
# never touched — there is no "enabled: false" anywhere in this module.
sword:

  # --- identity ----------------------------------------------------------
  material: DIAMOND_SWORD    # any Material name; defaults to STONE if omitted
  amount: 1                  # defaults to 1

  # Name and lore go through i18n: MiniMessage tags, PlaceholderAPI placeholders
  # and translation keys all work here. See ../i18n/i18n.md.
  name: '<gradient:#ff0000:#ffaa00>Ember Blade</gradient>'
  lore:
    - '<gray>Forged in the deep.'
    - '<gray>Damage: <red><damage></red>'   # <damage> filled by a TextReplacement

  # --- enchantments ------------------------------------------------------
  # One enchantment: "<key> <level>". The key may be namespaced, because this
  # attribute splits on whitespace only.
  enchantment: 'minecraft:sharpness 5'

  # Several: a section of arbitrarily-named children, each in either form.
  enchantments:
    unbreaking: 'minecraft:unbreaking 3'
    mending:
      enchantment: minecraft:mending
      level: 1

  # Fake enchantment glint. Ignored if the item already has real enchantments,
  # so on this item it does nothing — see "Ordering" below.
  glow: true

  # --- attribute modifiers ----------------------------------------------
  # "<attribute> <amount> <operation> <slot>". NOT namespaced — this one splits
  # on colons as well as spaces, so 'minecraft:generic.attack_damage' breaks.
  attribute: 'generic.attack_damage 10 ADD_NUMBER HAND'

  attributes:
    speed:
      attribute: generic.movement_speed
      amount: -0.05
      operation: ADD_SCALAR
      slot: HAND
      # uuid: optional; a random one is generated per load if omitted

  # --- durability --------------------------------------------------------
  durability: 1500           # remaining durability (maxDurability - damage)
  # damage: 61               # ...or raw damage taken. Don't use both.
  unbreakable: true
  repairCost: 3              # anvil prior-work penalty

  # --- presentation ------------------------------------------------------
  model: 100112              # CustomModelData
  flags:                     # any ItemFlag constants
    - HIDE_ATTRIBUTES
    - HIDE_UNBREAKABLE
```

Potions, leather armour and player heads use the same pattern:

```yaml
potion:
  material: SPLASH_POTION
  potionType: STRENGTH       # base potion type
  potionModifier: EXTENDED   # NONE | EXTENDED | UPGRADED
  potionColor: '#8B0000'     # hex, or a red/green/blue section
  potionEffect: 'REGENERATION 200 1 true true true'
  potionEffects:             # ...or several, in either form
    slow:
      type: SLOW
      duration: 100
      amplifier: 2

tunic:
  material: LEATHER_CHESTPLATE
  armorColor:                # hex '#RRGGBB' or component form
    red: 20
    green: 120
    blue: 200

head:
  material: PLAYER_HEAD
  skull: Notch               # a known player name (resolved to a UUID)
  # skull64: 'eyJ0ZXh0dXJlcyI6...'   # ...or a base64 texture value
  # skullPlayer: true                # ...or "whoever this item is built for"
```

## The attribute vocabulary

Registered in `ItemConfigurable.java:35-60`; keys defined in
`item/ItemAttribute.java:10-33`. Listed in registration order, which is also
application order.

| Key | Value | Applies to |
|---|---|---|
| `material` | `Material` name; defaults to `STONE` when the key is absent, rejected at load when unrecognised | the stack's type |
| `amount` | int, default 1 | stack size |
| `name` | string, through i18n | display name |
| `lore` | string list, through i18n | lore lines |
| `flags` | `ItemFlag` name list | item flags |
| `skull` | player name → `OfflinePlayer` | `SkullMeta` owner |
| `skull64` | base64 texture value | `SkullMeta` profile |
| `skullPlayer` | boolean | `SkullMeta` owner = the context player |
| `unbreakable` | boolean | unbreakable flag |
| `enchantment` | `<key> <level>`, or a section | one enchantment |
| `enchantments` | section of the above | many enchantments |
| `attribute` | `<attr> <amount> <op> <slot>`, or a section | one attribute modifier |
| `attributes` | section of the above | many modifiers |
| `glow` | boolean | fake glint via hidden `LURE` |
| `durability` | int | *remaining* durability |
| `damage` | int | raw damage value |
| `model` | int | `CustomModelData` |
| `repairCost` | int | anvil repair cost |
| `potionColor` | hex or RGB section | potion colour |
| `potionType` | `PotionType` name, default `WATER` | base potion data |
| `potionModifier` | `NONE`/`EXTENDED`/`UPGRADED` | base potion data |
| `potionEffect` | `<type> <duration> <amplifier> <ambient> <particles> <icon>`, or a section | one custom effect |
| `potionEffects` | section of the above | many custom effects |
| `armorColor` | hex or RGB section | leather armour colour |

`durability` and `damage` are two views of the same field: `durability` sets
`damage = maxDurability - value` (`item/attribute/DurabilityAttribute.java:28-35`),
`damage` sets it directly (`item/attribute/DamageAttribute.java:26-30`). Setting both
means the later-registered one — `damage` — wins.

Every attribute that needs a specific meta type checks first and no-ops otherwise:
`skull` on a non-skull, `armorColor` on non-leather, `potionType` on a non-potion are all
silently ignored rather than throwing. That makes the vocabulary safe to apply to any
material, at the cost of typos in the material line producing an item that quietly lacks
half its configuration.

## Building the stack

`build(ItemAttributeContext)` (`ItemConfigurable.java:78-88`) starts from
`new ItemStack(Material.STONE)` and applies every attribute implementing
`ItemStackAttribute`, in registration order.

Four behaviour interfaces decide how an attribute participates:

| Interface | Signature | Used by |
|---|---|---|
| `ItemStackAttribute` (`item/ItemStackAttribute.java:12-14`) | `apply(ItemStack)` | attributes changing the stack itself — `material`, `amount`, `durability` |
| `MetaAttribute` (`item/MetaAttribute.java:13-19`) | `apply(ItemMeta)`, defaulted through `editMeta` | most attributes |
| `ContextItemStackAttribute` (`item/ContextItemStackAttribute.java:14-20`) | `apply(ItemStack, ItemAttributeContext)` | attributes needing the player or replacements |
| `ContextMetaAttribute` (`item/ContextMetaAttribute.java:12-29`) | `apply(ItemMeta, ItemAttributeContext)` | `name`, `lore`, `skullPlayer` |

`build` checks for `ContextItemStackAttribute` first and passes the context along,
falling back to the context-free `apply` otherwise (`ItemConfigurable.java:81-86`).
`ManualAttribute` (`item/ManualAttribute.java:10-18`) is the odd one out: it is applied by
nothing during `build`, and exists so an attribute like a GUI `command` can be fired
deliberately outside a click.

### Ordering matters

Because application order is registration order (see
[Attributes and Containers](attributes.md#attribute-order-is-registration-order-not-yaml-order)),
two dependencies in the shipped vocabulary work out:

- `material` is applied first, so `durability` can read `getType().getMaxDurability()`.
  A `durability` on an item with no `material` is computed against `STONE`, whose max
  durability is 0.
- `enchantment`/`enchantments` are applied before `glow`, and `GlowAttribute` skips
  itself if `meta.hasEnchants()` (`item/attribute/GlowAttribute.java:33-39`). So `glow`
  means "glint *unless* there are real enchantments", and the example at the top of this
  page has no effect.

### Context and i18n

`ItemAttributeContext` (`item/ItemAttributeContext.java:15-37`) carries a nullable
`Player` plus `TextReplacement[]` and `ComponentReplacement[]`. The convenience overloads
`build(...)`, `build(Player, ...)`, `buildComponent(...)` (`ItemConfigurable.java:90-108`)
construct one for you.

`name` and `lore` resolve through `I18n.global.accessor(...)`
(`item/attribute/NameAttribute.java:29-36`, `item/attribute/LoreAttribute.java:35-50`),
which means the string in config can be a translation key, MiniMessage markup, or a
PlaceholderAPI placeholder — and the context player decides which language and whose
placeholders. Both branch on whether the configured audience is a
`BukkitComponentAudiences` and use the deprecated bungee-component API if so; see
[Interpreters and Validation](../i18n/interpreters.md).

`lore` skips itself entirely when its list is empty (`item/attribute/LoreAttribute.java:38`),
so `lore: []` does not clear existing lore.

## Sharp edges

- **`build` needs a live server.** It calls `getItemMeta()`/`editMeta`, so an
  `ItemConfigurable` can be parsed without a server but not built. Parsing enchantments
  and potion types also needs the server's registries to be populated.
- **`enchantment` and `attribute` disagree about namespacing.**
  `EnchantmentAttribute.Factory.fromString` splits with `Args.SPACE_DELIMITER`
  (`item/attribute/EnchantmentAttribute.java:68-80`), so `minecraft:sharpness 5` works.
  `GenericAttribute.Factory.fromString` uses the default colon-or-space delimiter
  (`item/attribute/GenericAttribute.java:84-102`), so `minecraft:generic.attack_damage 10`
  splits into four tokens and fails with `NullPointerException: Invalid attribute`.
  Verified. Use the unqualified name in `attribute`, or the section form.
- **A bad material, enchantment, attribute, potion type, potion modifier or skull name
  aborts the config load.** These factories use Guava `Preconditions.checkNotNull`, so the
  failure is a `NullPointerException` propagating out of `fromConfig` — it does not skip
  the one bad key. The message names the offending value and the config path, e.g.
  `Invalid material 'NOT_A_BLOCK' at rewards.sword.material`. Verified.
- **`skull` requires a name the server has seen.** `Bukkit.getPlayerUniqueId` returning
  null throws (`item/attribute/SkullAttribute.java:49-56`). Prefer `skull64` for a fixed
  texture.
- **`Skull64Attribute` caches profiles in a static `HashMap` that never evicts**
  (`item/attribute/Skull64Attribute.java:29`, `30-41`), keyed by the base64 string. Fine
  for a fixed set of config-declared heads; not fine if you build them from user input.
- **`attributes` generates a fresh random UUID per modifier per load** when `uuid` is
  omitted (`item/attribute/GenericAttribute.java:73`, `74`). Two items built from the
  same config after a reload carry different modifier UUIDs, so they will not stack and
  vanilla will not treat the modifiers as equal.
- **`glow` adds `HIDE_ENCHANTS` whether you asked for it or not.** It fakes the glint
  with a real `Enchantment.LURE` and hides it (`item/attribute/GlowAttribute.java:33-39`).
  Since `flags` is applied first and `addItemFlags` is additive, a glowing item always
  hides its enchantment lines — including any you configured deliberately.
- **Round-tripping loses the section form.** Generated config always renders the compact
  string form, so a section-form `enchantments` entry comes back as
  `'minecraft:mending 1'`, and an `armorColor`/`potionColor` written as `red`/`green`/`blue`
  comes back as `'#RRGGBB'`. Verified; both re-parse to the same value, but the file text
  changes.

## See also

- [Attributes and Containers](attributes.md) — how these keys are resolved, and how to
  add your own.
- [GUIs](guis.md) — `GuiConfigurable` and `InvUiItemConfigurable`, both subclasses of
  `ItemConfigurable`.
- [WUtils Internationalization](../i18n/i18n.md) — what `name` and `lore` are run
  through.
- [Items](../common/items.md) — `common`'s lower-level `ItemUtils`, unrelated but easy to
  confuse.
