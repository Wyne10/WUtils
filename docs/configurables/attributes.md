# Attributes and Containers

This page is the framework behind [Items](items.md), [Interactions](interactions.md),
[Animations](animations.md) and [GUIs](guis.md). Read it if you want to understand why a
YAML key does what it does, or if you want to add a key of your own.

Everything here lives in
`configurables/src/main/java/me/wyne/wutils/config/configurables/attribute/`.

## The attribute hierarchy

| Type | Adds |
|---|---|
| `Attribute<V>` | the interface: `getKey()`, `getValue()` — both `@NotNull` (`Attribute.java:15-29`) |
| `AttributeBase<V>` | a final-field implementation of both (`AttributeBase.java:13-31`) |
| `ConfigurableAttribute<V>` | also `CompositeConfigSerializable`, so it can render itself back into generated YAML as `key: value` (`ConfigurableAttribute.java:25-28`) |
| `CompositeAttribute<V extends Attribute<?>>` | a `ConfigurableAttribute<Set<V>>` — an attribute whose value is a nested collection of attributes (`CompositeAttribute.java:26-54`) |

The split between `AttributeBase` and `ConfigurableAttribute` is exactly the split
between "readable" and "writable". Only `ConfigurableAttribute`s appear in generated
config: `AttributeContainerBase.toConfig` collects `getSet(ConfigurableAttribute.class)`
and asks each to render itself (`AttributeContainerBase.java:195-204`). An attribute
extending plain `AttributeBase` — `PrimitiveAttribute`, `RootAttribute`,
`GuiActionAttribute` — is invisible to config generation by design, because it either has
no sensible text form or was never read from text in the first place.

`ConfigurableAttribute.toConfig` renders `key: value` through
[`ConfigBuilder`](../config/serialization.md), which quotes strings and expands
collections. Attributes whose value is a record or a Bukkit object override it — see
`configurables/src/main/java/me/wyne/wutils/config/configurables/attribute/common/SoundAttribute.java:29-32`,
which flattens an Adventure `Sound`
back into `key volume pitch source`.

### Composite attributes

`CompositeAttribute` is how one YAML key holds many of the same thing. Its
config constructor (`CompositeAttribute.java:32-39`) reads the section at `key`, and
builds one child attribute per child key, using the child's own name as the child
attribute's key. `EnchantmentsAttribute`, `PotionEffectsAttribute`, `GenericsAttribute`,
`ColorsAttribute` and `FireworkEffectsAttribute` are all one-line subclasses of it.

The child names are arbitrary labels — they exist only so YAML can hold a list of
sections. Nothing reads them back.

## Factories

| Type | Contract |
|---|---|
| `GenericFactory<T>` | `T create(String key, ConfigurationSection config)` (`GenericFactory.java:17-19`) |
| `AttributeFactory<T extends Attribute<?>>` | the same, narrowed to attributes (`AttributeFactory.java:10`) |
| `CompositeGenericFactory<T>` / `CompositeAttributeFactory<T>` | a `create` that dispatches on the *shape* of the YAML value |

Note what a factory receives: the key, and **the section that key lives in** — not the
value. That is what lets one factory look at `config.getString(key)`,
`config.getConfigurationSection(key)`, or ignore the key and read sibling keys instead.

### The composite dispatch

`CompositeAttributeFactory.create` (`CompositeAttributeFactory.java:21-29`) picks one of
three paths:

1. the value at `key` is a section → `fromSection(key, thatSection)`
2. the value at `key` is a string → `fromString(key, thatString, config)`
3. otherwise → `fromSection(key, config)` — the *enclosing* section is treated as the
   attribute's own body

This is why almost every richly-structured attribute in this module accepts both a terse
one-line form and an expanded form. Case 3 is also what lets a composite-backed attribute
take an alias body that names its own fields — see below.

`fromString` implementations parse with [`Args`](../common/utilities.md). Read that page
before writing one: the default delimiter is **colon *or* whitespace**, and only
`Args.SPACE_DELIMITER` splits on whitespace alone. Several shipped attributes are
affected — see the sharp edges.

## AttributeMap: how YAML keys are resolved

`AttributeMap` holds an insertion-ordered `LinkedHashMap` of key → factory
(`AttributeMap.java:48-67`). `createAllMap` (`AttributeMap.java:87-96`) produces the
`Map<String, Attribute<?>>` a container is built from, and
`getAttributeKeyMap` (`AttributeMap.java:104-121`) is the resolver that decides which
config keys feed which factory. It runs two passes over the section:

**Pass 1 — direct keys.** For every registered key that the section `contains`, the
config key *is* the registered key, and the factory is handed the enclosing section
(`AttributeMap.java:108-110`, `48-52`).

**Pass 2 — `attributeType` aliases.** Every direct child that is itself a section, has a
string `attributeType`, and whose `attributeType` names a registered key, is registered
as an *additional* occurrence of that key under its own name (`AttributeMap.java:111-119`).
Aliased keys are marked "typed", and a typed key's factory is handed **its own child
section** rather than the enclosing one (`AttributeMap.java:98-102`).

`attributeType` is the escape hatch for "I need two of these". A registered key can
appear only once, because it is a YAML key; an alias can appear as often as you like
under any name.

<!-- allow-code-fences -->

```yaml
# Both of these produce a "sound" attribute.
sound:                        # direct form — the registered key itself
  sound: BLOCK_CHEST_OPEN
  volume: 0.5

openSound:                    # aliased form — any name you like
  attributeType: sound        # ...as long as it declares which attribute it is
  sound: BLOCK_CHEST_OPEN
  volume: 0.5
```

### What an alias actually changes

One substitution explains every case. Where the direct form calls the factory with the
registered key and the *enclosing* section, the aliased form calls it with the alias name
and the *alias's own* section:

| | factory is called as |
|---|---|
| direct `sound:` | `create("sound", <enclosing section>)` |
| aliased `openSound:` | `create("openSound", <the openSound section>)` |

So whatever the factory would have read out of the enclosing section at the registered
key, it now reads out of the alias section at the alias name. **Every attribute can be
aliased** — what differs is the shape the alias body has to take, and that is decided by
how its factory reads.

### Two alias body shapes

**A factory that reads a value at the key** — `config.getString(key)`,
`ConfigUtils.getStringList(config, key)` — needs the alias body to carry a key *named
after the alias itself*. The YAML mirrors itself one level down:

```yaml
# structure's `set` modifier: SetEditModifier.Factory does config.getString(key, "")
set1:
  attributeType: set
  set1: 'crying_obsidian 30%obsidian,35%crying_obsidian,35%ancient_debris'
set2:
  attributeType: set
  set2: 'obsidian 84%obsidian,10%crying_obsidian,5%ancient_debris,1%air'
set3:
  attributeType: set
  set3: 'white_concrete obsidian'
```

That repetition looks redundant, but it is exactly the substitution above: `key` is now
`set1`, and the factory reads `set1` out of the `set1` section. Verified against the
**unmodified shipped factories** — an aliased `altMaterial` carrying
`altMaterial: DIAMOND` parses to `DIAMOND` through `MaterialAttribute.Factory`, and an
aliased `extraMessage` carrying an `extraMessage:` list parses to that list through
`MessageAttribute.Factory`.

**A factory that resolves its body section** takes the fields directly, with no
repetition:

```yaml
openSound:
  attributeType: sound
  sound: BLOCK_CHEST_OPEN
  volume: 0.5
```

Two ways to get that. `CompositeAttributeFactory` does it for free: `openSound` is not a
key inside its own section, so dispatch falls through to case 3, `fromSection(key, config)`,
which reads the enclosing section as the body. Or do it in one line with
[`ConfigUtils.getConfigurationSection`](../common/config-utils.md)
(`common/src/main/java/me/wyne/wutils/common/config/ConfigUtils.java:42-46`), which
returns the child section at `path` **or `section` itself when `path` is not a section**:

```java
public static final class Factory implements AttributeFactory<RegionAttribute> {
    @Override
    public RegionAttribute create(String key, ConfigurationSection config) {
        var section = ConfigUtils.getConfigurationSection(config, key);   // <- the whole trick
        return new RegionAttribute(key, section.getString("world"), section.getInt("margin"));
    }
}
```

Direct form → `section` is the child; aliased form → `section` is the alias's own body.
This is the idiom the `structure` module uses throughout — see
`structure/src/main/java/me/wyne/wutils/structure/location/RandomLocation.java:43-56`,
`structure/src/main/java/me/wyne/wutils/structure/region/MarginRegion.java:47-53`, and the
dispatching factories in
`structure/src/main/java/me/wyne/wutils/structure/region/StructureRegion.java:34-43` and
`structure/src/main/java/me/wyne/wutils/structure/scheme/Scheme.java:43-52`, which use it
to decide *which* implementation to build before reading anything.

Verified with a plain (non-composite) `AttributeFactory` against a config carrying both a
direct `region:` and an aliased `spawnRegion: {attributeType: region, …}`:

| Factory body | direct `region` | aliased `spawnRegion` |
|---|---|---|
| `config.getString(key + ".world")` | `overworld/5` | `<missing>/-1` |
| `ConfigUtils.getConfigurationSection(config, key)` | `overworld/5` | `nether/9` |

### Getting the shape wrong fails silently

Using the second shape with a factory that wants the first is the trap. An aliased
`material` section carrying `material: DIAMOND` parses to `STONE`, and an aliased
`message` section carrying `message: 'aliased'` parses to an empty list — in both cases
the factory looked for the *alias name* inside the alias section, found nothing, and
returned its default. Verified; no warning is logged.

The fix is to write the body in the shape that attribute's factory expects: repeat the
alias name (`altMaterial: DIAMOND`) for a read-at-the-key factory, or name the fields
directly for a section-resolving one. If you own the factory and want both to work,
implement `CompositeAttributeFactory` — `fromString` covers `key: scalar` and
`fromSection` covers a named body.

### Attribute order is registration order, not YAML order

`createAllMap` iterates the *registry's* key set, not the config's
(`AttributeMap.java:90-94`), and collects into a `LinkedHashMap`. So the resulting
attribute order — and therefore the order in which `ItemConfigurable` applies attributes
to an `ItemStack`, or `InteractionConfigurable` builds its audience — follows the order
the factories were registered in, regardless of how the YAML was written.

That is load-bearing, not incidental. `ITEM_ATTRIBUTE_MAP` registers `material` first so
`DurabilityAttribute` can read `item.getType().getMaxDurability()`, and registers
`enchantment`/`enchantments` before `glow` so `GlowAttribute`'s "skip if already
enchanted" check sees real enchantments (`ItemConfigurable.java:36-59`). Reordering that
static block changes behaviour.

### GenericFactoryMap

`GenericFactoryMap<T>` (`GenericFactoryMap.java:21-91`) is a line-for-line copy of
`AttributeMap` with the `Attribute` bound removed, for building non-attribute objects
from the same key/`attributeType` scheme. Nothing in this module uses it; it is there for
consumers. Any behaviour described above applies to it identically, including the two
alias body shapes.

`AttributeUtils.createAll` (`AttributeUtils.java:21-25`) is the unrelated simple case:
run one factory over *every* child key of a section, no registry involved. That is how
you parse "a section of N things that are all the same kind".

## Containers

`AttributeContainer` (`AttributeContainer.java:29-138`) is the queryable bag.
`AttributeContainerBase` implements the query surface; the two concrete classes differ
only in whether the mutators copy.

### Reading

Every lookup comes in a by-key and a by-class flavour, and in three depths:

| Method | Returns |
|---|---|
| `get(key)` / `get(Class)` | the attribute, cast to the requested type |
| `getAttribute(key)` / `getAttribute(Class)` | the attribute as `Attribute<V>` |
| `getValue(key)` / `getValue(Class)` | `attribute.getValue()` |
| `getSet(Class)` / `getAttributes(Class)` / `getValues(Class)` | all matches, in registration order |

Each has a `(…, def)` overload; the no-default overloads simply pass `null`
(`AttributeContainerBase.java:73-81`). The by-class lookups are `instanceof` scans over
the values (`AttributeContainerBase.java:83-106`) — this is how `getSet(ItemStackAttribute.class)`
picks out "every attribute that knows how to modify an item".

Note that `getValue(String key, V def)` returns `def` when the *attribute* is missing,
and the attribute's value when present (`AttributeContainerBase.java:163-170`). Those are
the only two outcomes: `Attribute.getValue()` is `@NotNull`, so a present attribute never
yields `null`.

The container's own `getValue(key)` stays `@Nullable`, but for a different reason — the
attribute may be **absent**. That is this module's one "not set" signal, and it is why an
attribute is never built around a `null`: a value that cannot be resolved is a config
error the factory rejects at load, not a third state.

### The `root` attribute

`fromConfig` (`AttributeContainerBase.java:217-224`) clears the map, inserts a
`RootAttribute` under the key `"root"` holding the entire section, and only then runs the
attribute map. `getRoot()` (`AttributeContainerBase.java:56-59`) hands it back.

Two consequences. First, `root` is always the first entry, and always present after a
config read — code iterating `getAttributes()` sees it. Second, it is your escape hatch:
if a config carries keys this module knows nothing about, `getRoot()` gets you the raw
`ConfigurationSection` to read them from. `RootAttribute` extends plain `AttributeBase`,
so it never appears in generated output.

A container built by hand — via a builder, or `with(...)` — has no `root` unless you add
one. `InteractionListConfigurable`'s string and list shorthands take that path, so their
interactions have no `root` (`InteractionListConfigurable.java:64-79`).

### Immutable vs mutable

| | `ImmutableAttributeContainer` | `MutableAttributeContainer` |
|---|---|---|
| `with(...)`, `ignore(...)` | copy, modify the copy, return it (`ImmutableAttributeContainer.java:45-80`) | modify in place, return `this` (`MutableAttributeContainer.java:39-69`) |
| built by | `AttributeContainerBuilder.buildImmutable()` | `AttributeContainerBuilder.build()` |
| used by | every shipped configurable | nothing shipped |

**"Immutable" describes the mutator API, not the object.** The copy constructors take a
shallow `new LinkedHashMap<>(...)` (`AttributeContainerBase.java:51-54`), and
`getAttributes()` returns the live map, so
`immutableContainer.getAttributes().put(...)` mutates it. The guarantee you actually get
is that `with`/`ignore` do not disturb the container you called them on — which is what
matters, since the shipped configurables share a static `AttributeMap` between every
instance.

`AttributeContainerBuilder` (`AttributeContainerBuilder.java:13-74`) is the fluent front
end, and can produce either kind from the same accumulated state.

## Accessors

An `AttributeConfigurable` wraps a container but is not one, so `with`/`ignore` cannot
return the right static type. `AttributeConfigurableAccessor<T>` fixes that: it applies
the container operation and hands back the *configurable*, correctly typed
(`AttributeConfigurable.java:55-68`).

- `getImmutableAccessor()` builds a new configurable per operation, by reflectively
  calling a `T(AttributeContainer)` constructor
  (`ImmutableAttributeConfigurableAccessor.java:28-35`, `61-67`). Every shipped
  configurable declares one. A custom subclass without it throws
  `RuntimeException` from the accessor's constructor, before you call anything on it.
- `getMutableAccessor()` mutates the configurable's container and returns the same
  instance.

Use these to derive variants of a config-loaded object — a per-player copy of a GUI
button with the amount changed, say — without re-reading config:

```yaml
# config
reward:
  material: DIAMOND
  name: '<gold>Daily reward'
```

```java
// Java: same button, 64 of them, original untouched
ItemConfigurable stack = reward.<ItemConfigurable>getImmutableAccessor()
        .with(new AmountAttribute(64));
```

## Adding your own attribute

Three pieces: the attribute class, its factory, and a registration.

```java
public class CooldownAttribute extends ConfigurableAttribute<TimeSpan> {

    public CooldownAttribute(String key, TimeSpan value) {
        super(key, value);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().toString()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory<CooldownAttribute> {
        @Override
        public CooldownAttribute create(String key, ConfigurationSection config) {
            return new CooldownAttribute(key, Durations.getTimeSpan(config.getString(key, "0t")));
        }
    }
}

// once, during plugin enable:
ItemConfigurable.ITEM_ATTRIBUTE_MAP.put("cooldown", new CooldownAttribute.Factory());
```

Then `cooldown: 30s` works on every item, and `item.<TimeSpan>getValue("cooldown")`
reads it back.

To make the attribute *do* something rather than just carry a value, implement the
relevant behaviour interface too — `ItemStackAttribute` / `MetaAttribute` for items,
`ContextInteractionAttribute` for interactions, `ContextAnimationAttribute` for animation
steps, `ClickEventAttribute` for GUI buttons. The configurable's `build`/`send` picks up
anything implementing them.

Extend `ConfigurableAttribute` rather than `AttributeBase` unless you specifically want
the attribute excluded from generated config. The factory above reads a value at the key,
so an alias of it has to repeat the alias name
(`shortCooldown: {attributeType: cooldown, shortCooldown: 5s}`). To let an alias name its
fields instead, resolve the body with `ConfigUtils.getConfigurationSection(config, key)`;
to support both shapes, implement `CompositeAttributeFactory`.

### Registration ordering

Two traps, both from the static registries:

- **Register before the first config read.** `Config#reloadConfig` calls `fromConfig`,
  which runs the map as it stands at that moment. Registering afterwards affects only
  subsequent reads.
- **Register before the derived maps are initialised.** `GUI_ITEM_ATTRIBUTE_MAP` and
  `INV_UI_ITEM_ATTRIBUTE_MAP` copy `ITEM_ATTRIBUTE_MAP` in their own static initialisers
  (`GuiConfigurable.java:35-41`, `InvUiItemConfigurable.java:25-28`). A key added to
  `ITEM_ATTRIBUTE_MAP` after `GuiConfigurable` has been class-loaded will not reach GUI
  items. Add to all the maps you care about explicitly.

## Sharp edges

- **An `attributeType` alias body written in the wrong shape silently yields the
  default.** Covered above; this is the one most likely to cost you an afternoon. Nothing
  logs it — the attribute is simply built from its defaults.
- **A composite attribute pointed at a non-section throws `NullPointerException`.**
  `CompositeAttribute`'s constructor checks `config.contains(key)` and then calls
  `getKeys(false)` on `config.getConfigurationSection(key)`, which is null if the value
  is a string or a list (`CompositeAttribute.java:35-38`). Writing
  `enchantments: 'minecraft:sharpness 5'` instead of `enchantment:` NPEs during config
  load. Verified.
- **`MutableAttributeConfigurableAccessor.with` and `ignore` are no-ops over an
  `ImmutableAttributeContainer`** — which is what every shipped configurable uses. They
  delegate to the container's `with`/`ignore`, which on the immutable implementation
  return a modified *copy* and leave the receiver untouched, and the accessor discards
  that return value. Verified: `getMutableAccessor().with(new AmountAttribute(64))` on an
  `ItemConfigurable` leaves the amount at its original value, and `ignore("material")`
  leaves the attribute in place; both work when the configurable was built over a
  `MutableAttributeContainer`. `copy(AttributeContainer)` had the same defect and is now
  fixed — it writes through the live maps instead of delegating
  (`MutableAttributeConfigurableAccessor.java:82-94`).
- **`GenericFactoryMap` is a full copy of `AttributeMap`.** Any fix to one has to be made
  twice.
- **`AttributeUtils.createAll` uses raw types** (`AttributeUtils.java:21`), so its `T` is
  unchecked — a mismatched factory fails with a `ClassCastException` at the call site,
  not at parse.

## See also

- [WUtils Configurables](configurables.md) — the module overview and the concept map.
- [Serialization](../config/serialization.md) — `ConfigBuilder`, and the
  `toConfig`/`fromConfig` contract these implement.
- [Core Utilities](../common/utilities.md) — `Args`, whose delimiter rules govern every
  string-form attribute.
