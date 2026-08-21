# Writing Your Own Configurable

[Configurables](configurables.md) ships items, interactions, animations and GUI buttons
with a fixed but large vocabulary of YAML keys. Most of the time, extending that
vocabulary — adding one new key that works on every item in your plugin, say — is all
you need, and that's what this page focuses on. Building an entirely new configurable
type from scratch is rarer; for that, [Custom Config Types](../config/serialization.md)
covers the lower-level `toConfig`/`fromConfig` contract every configurable here is built
on.

## What an attribute is

`ItemConfigurable`, `InteractionConfigurable`, `AnimationStepConfigurable` and the GUI
configurables are all, under the hood, a bag of **attributes** — one attribute per YAML
key the config actually contains. An attribute is just a key and a value:

```java
public interface Attribute<V> {
    String getKey();
    V getValue();
}
```

Everything an attribute *does* comes from extra interfaces it implements on top of that.
`AmountAttribute` is an `Attribute<Integer>` that also implements `ItemStackAttribute`,
so it knows how to set an `ItemStack`'s stack size. The container holding a group of
attributes never needs to know what any individual one means — it just holds them and
hands out the ones matching whatever interface you ask for.

Extend `ConfigurableAttribute<V>` (not the lower-level `AttributeBase<V>`) for an
attribute you want to appear in generated config. `AttributeBase` is for read-only,
never-serialized attributes — you won't need it for your own attributes unless you
specifically want one excluded from generated output.

## Factories: turning a YAML key into an attribute

An `AttributeFactory<T>` builds a `T` from a key and **the section that key lives in** —
not the value at the key:

```java
public interface AttributeFactory<T extends Attribute<?>> {
    T create(String key, ConfigurationSection config);
}
```

Receiving the enclosing section rather than just the value is what lets a factory read
`config.getString(key)`, `config.getConfigurationSection(key)`, or ignore the key
entirely and read a sibling key instead — whatever shape that particular attribute
needs.

## A complete custom attribute

Say you want every item in your plugin to optionally carry a use cooldown: `cooldown:
30s`, readable back as a `TimeSpan`.

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
```

Register the factory once, during plugin startup, before you read any config:

```java
ItemConfigurable.ITEM_ATTRIBUTE_MAP.put("cooldown", new CooldownAttribute.Factory());
```

From then on, `cooldown: 30s` works inside any item section:

```yaml
sword:
  material: DIAMOND_SWORD
  cooldown: 30s
```

```java
TimeSpan cooldown = ShopConfig.sword.getValue("cooldown");
```

That's a readable value — carrying data, but not doing anything by itself. To make it
*act* on the item, also implement the relevant behavior interface: `ItemStackAttribute`
(or `MetaAttribute`) for items, `ContextInteractionAttribute` for interactions,
`ContextAnimationAttribute` for animation steps, `ClickEventAttribute` for GUI buttons.
`ItemConfigurable#build` (and the equivalent on the other configurables) automatically
picks up anything implementing the interface it cares about — see [Item
Configurables](items.md), [Interaction Configurables](interactions.md), [Animation
Configurables](animations.md) and [GUI Configurables](guis.md) for what each interface
requires.

### Registration has to happen early

Two ordering traps, both a consequence of these maps being static and populated once:

- **Register before the first config load.** Reloading config runs the attribute map as
  it stands at that moment; registering a factory afterward only affects reads that
  happen later.
- **Register into every map you care about, explicitly.** `GuiConfigurable` and
  `InvUiItemConfigurable` each copy `ItemConfigurable.ITEM_ATTRIBUTE_MAP` into their own
  map when their class is first loaded. A key you add to `ITEM_ATTRIBUTE_MAP` *after*
  `GuiConfigurable` has already been loaded will work on plain items but silently never
  reach GUI items — you have to `put` it into `GuiConfigurable.GUI_ITEM_ATTRIBUTE_MAP`
  (and `InvUiItemConfigurable.INV_UI_ITEM_ATTRIBUTE_MAP`) too, if you want it there.

## `attributeType`: giving one key more than one occurrence

A registered key can only appear once in a YAML section, because it's a YAML key.
`attributeType` is the escape hatch for needing several of the same attribute under
different names — any section that carries a string `attributeType` naming a registered
key is treated as an extra occurrence of that key:

```yaml
sound:                        # direct form — the registered key itself
  sound: BLOCK_CHEST_OPEN
  volume: 0.5

openSound:                    # aliased form — any name you like
  attributeType: sound        # ...as long as it names the attribute it aliases
  sound: BLOCK_CHEST_OPEN
  volume: 0.5
```

### The alias body has to match how the factory reads

Whichever section your factory would normally read from, the aliased form hands it the
*alias's own* section instead. That changes what shape the alias body needs, and getting
it wrong is the trap most likely to cost you time, because **nothing is logged when it
happens** — the attribute is simply built from its defaults.

- A factory that reads a value **at the key itself** (`config.getString(key)`, like the
  `CooldownAttribute` above) needs the alias body to repeat the alias's own name:

  ```yaml
  shortCooldown:
    attributeType: cooldown
    shortCooldown: 5s
  ```

- A factory that resolves its own body section (via `ConfigUtils.getConfigurationSection`,
  or by implementing `CompositeAttributeFactory`, whose fallback branch does this for
  free) takes the fields directly, with no repetition — as `openSound` does above.

If you write `openSound: {attributeType: sound, sound: BLOCK_CHEST_OPEN}` against a
factory that expects the repeat-the-key shape, or vice versa, you don't get an error —
you get an attribute quietly holding its constructor defaults. If you own the factory and
want both alias shapes to work, implement `CompositeAttributeFactory`, whose `fromString`
covers a scalar body and `fromSection` covers a named one.

## Composite attributes: one key holding many of the same thing

`CompositeAttribute<V>` is the base for "a section of several children, all the same
kind" — `EnchantmentsAttribute`, `PotionEffectsAttribute` and similar in the shipped item
attributes are one-line subclasses of it. Its config constructor reads the section at
its key and builds one child attribute per child key, using each child's own name as a
label; the labels are arbitrary and nothing reads them back.

**Point it at a string or a list instead of a section and it throws
`NullPointerException` during config load.** `enchantments: 'minecraft:sharpness 5'`
(a string, meant for the singular `enchantment` key) instead of a proper `enchantments:`
section fails this way — the constructor asks the non-existent child section for its
keys before it has a chance to tell you it isn't a section at all.

## Containers and accessors

Every attribute-backed configurable wraps an `AttributeContainer` — a queryable bag
pairing the attribute map with whatever attributes parsing produced. You'll mostly reach
it through the configurable itself (`itemConfigurable.getValue("cooldown")`,
`.contains("cooldown")`, `.getSet(ItemStackAttribute.class)`), which delegates to its
container for you.

There are two container implementations:

| | `ImmutableAttributeContainer` | `MutableAttributeContainer` |
|---|---|---|
| `with(...)` / `ignore(...)` | returns a **new** container, leaves the receiver untouched | mutates in place, returns the same instance |
| used by | every shipped configurable (`ItemConfigurable`, `InteractionConfigurable`, ...) | nothing shipped — opt in yourself if you want in-place mutation |

Since every configurable you get back from `fromConfig` is immutable-backed, deriving a
variant means building a new instance, not mutating the one you have. That's what
**accessors** are for — they apply a container operation and hand back the right
concrete type:

```java
// same button, 64 of them, the config-loaded original untouched
ItemConfigurable stack = reward.<ItemConfigurable>getImmutableAccessor()
        .with(new AmountAttribute(64));
```

`getImmutableAccessor()` reflectively calls a `T(AttributeContainer)` constructor on your
configurable's own class to build the copy — every shipped configurable declares one. If
you subclass `AttributeConfigurable` yourself and skip that constructor, the accessor
throws a `RuntimeException` the moment you try to use it, before any operation runs.

**`getMutableAccessor().with(...)`/`.ignore(...)` are silent no-ops when the underlying
container is immutable** — which, again, is every shipped configurable. The mutable
accessor dutifully calls `with`/`ignore` on the container, but the immutable
implementation returns a *new* container rather than mutating itself, and the accessor
throws that return value away instead of using it. Calling
`item.getMutableAccessor().with(new AmountAttribute(64))` on a normal, config-loaded
`ItemConfigurable` leaves its amount exactly as it was. Use `getImmutableAccessor()`
unless you built the configurable yourself over a genuinely mutable container.

## `AttributeUtils.createAll`: the simple case, without a registry

Not every "section of things" needs an `AttributeMap`. `AttributeUtils.createAll(config,
factory)` runs one factory over *every* direct child key of a section — no registration,
no `attributeType`, just "parse everything here the same way." It uses a raw
`AttributeFactory` internally, so if you hand it a factory whose type doesn't match what
you assign the result to, you get a `ClassCastException` at your call site rather than
at parse time — there's no runtime check tying the factory to the type you asked for.

## Sharp edges

- **An `attributeType` alias body in the wrong shape yields silent defaults** — the
  single most time-consuming trap in this module, and covered in full above.
- **A composite attribute pointed at a string or list throws `NullPointerException`**
  during config load, not at some later point where you'd expect a friendlier error.
- **`getMutableAccessor().with(...)`/`.ignore(...)` are no-ops on every shipped
  configurable**, because they're all built on `ImmutableAttributeContainer`. Reach for
  `getImmutableAccessor()` instead.
- **`AttributeUtils.createAll` fails with `ClassCastException` at the call site**, not at
  parse time, if the factory you pass doesn't actually produce the type you're assigning
  it to.
- **Register a new attribute factory before the first config read, and into every
  derived map you care about** (`GUI_ITEM_ATTRIBUTE_MAP`, `INV_UI_ITEM_ATTRIBUTE_MAP`
  copy `ITEM_ATTRIBUTE_MAP` once, at class-load time — they don't see later additions).

## See also

- [Configurables](configurables.md) — the module overview and the built-in vocabulary
  you're extending.
- [Custom Config Types](../config/serialization.md) — the lower-level
  `toConfig`/`fromConfig` contract and `ConfigBuilder`, for building a configurable type
  that isn't attribute-based at all.
- [Item Configurables](items.md), [Interaction Configurables](interactions.md),
  [Animation Configurables](animations.md), [GUI Configurables](guis.md) — the behavior
  interfaces (`ItemStackAttribute` and friends) your custom attribute implements to
  actually do something.
