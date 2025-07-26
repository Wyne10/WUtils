package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.item.*;
import me.wyne.wutils.config.configurables.item.attribute.*;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ItemConfigurable implements CompositeConfigurable {

    public final static AttributeMap ITEM_ATTRIBUTE_MAP = new AttributeMap(new LinkedHashMap<>());

    static {
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.MATERIAL.getKey(), new MaterialAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.AMOUNT.getKey(), new AmountAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.NAME.getKey(), new NameAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.LORE.getKey(), new LoreAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.FLAGS.getKey(), new FlagsAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SKULL.getKey(), new SkullAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SKULL64.getKey(), new Skull64Attribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SKULL_PLAYER.getKey(), new SkullPlayerAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.UNBREAKABLE.getKey(), new UnbreakableAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ENCHANTMENT.getKey(), new EnchantmentAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ENCHANTMENTS.getKey(), EnchantmentsAttribute::new);
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ATTRIBUTE.getKey(), new GenericAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ATTRIBUTES.getKey(), GenericsAttribute::new);
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.GLOW.getKey(), new GlowAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.DURABILITY.getKey(), new DurabilityAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.DAMAGE.getKey(), new DamageAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.MODEL.getKey(), new CustomModelDataAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.REPAIR_COST.getKey(), new RepairCostAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_COLOR.getKey(), new PotionColorAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_TYPE.getKey(), new PotionTypeAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_MODIFIER.getKey(), new PotionModifierAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_EFFECT.getKey(), new PotionEffectAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_EFFECTS.getKey(), PotionEffectsAttribute::new);
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ARMOR_COLOR.getKey(), new ArmorColorAttribute.Factory());
    }

    private final AttributeContainer attributeContainer;

    public ItemConfigurable() {
        attributeContainer = new ImmutableAttributeContainer(ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>());
    }

    public ItemConfigurable(ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    public ItemConfigurable(AttributeContainer attributeContainer) {
        this.attributeContainer = attributeContainer;
    }

    public ItemConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        this(attributeContainer);
        fromConfig(section);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return attributeContainer.toConfig(depth, configEntry);
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        attributeContainer.fromConfig(configObject);
    }

    public ItemStack build(ItemAttributeContext context) {
        var itemStack = new ItemStack(Material.STONE);
        attributeContainer.getSet(ItemStackAttribute.class)
                .forEach(attribute -> {
                    if (attribute instanceof ContextItemStackAttribute)
                        ((ContextItemStackAttribute) attribute).apply(itemStack, context);
                    else
                        attribute.apply(itemStack);
                });
        return itemStack;
    }

    public ItemStack build(TextReplacement... textReplacements) {
        var context = new ItemAttributeContext(null, textReplacements, new ComponentReplacement[]{});
        return build(context);
    }

    public ItemStack build(Player player, TextReplacement... textReplacements) {
        var context = new ItemAttributeContext(player, textReplacements, new ComponentReplacement[]{});
        return build(context);
    }

    public ItemStack buildComponent(ComponentReplacement... componentReplacements) {
        var context = new ItemAttributeContext(null, new TextReplacement[]{}, componentReplacements);
        return build(context);
    }

    public ItemStack buildComponent(Player player, ComponentReplacement... componentReplacements) {
        var context = new ItemAttributeContext(player, new TextReplacement[]{}, componentReplacements);
        return build(context);
    }

    public ItemConfigurable ignore(ItemAttribute... ignore) {
        return new ItemConfigurable(attributeContainer.ignore(Arrays.stream(ignore).map(ItemAttribute::getKey).toArray(String[]::new)));
    }

    public ItemConfigurable ignore(String... ignore) {
        return new ItemConfigurable(attributeContainer.ignore(ignore));
    }

    public ItemConfigurable with(String key, AttributeFactory factory) {
        return new ItemConfigurable(attributeContainer.with(key, factory));
    }

    public ItemConfigurable with(Map<String, AttributeFactory> keyMap) {
        return new ItemConfigurable(attributeContainer.with(keyMap));
    }

    public ItemConfigurable with(Attribute<?> attribute) {
        return new ItemConfigurable(attributeContainer.with(attribute));
    }

    public ItemConfigurable with(AttributeContainer container) {
        return new ItemConfigurable(attributeContainer.with(container));
    }

    public ItemConfigurable with(ItemConfigurable itemConfigurable) {
        return new ItemConfigurable(attributeContainer.with(itemConfigurable.getAttributeContainer()));
    }

    public ItemConfigurable copy(AttributeContainer container) {
        return new ItemConfigurable(attributeContainer.copy(container));
    }

    public ItemConfigurable copy(ItemConfigurable itemConfigurable) {
        return new ItemConfigurable(attributeContainer.copy(itemConfigurable.getAttributeContainer()));
    }

    public ItemConfigurable copy() {
        return new ItemConfigurable(attributeContainer.copy());
    }

    @Nullable
    public <T> T get(String key) {
        return attributeContainer.get(key);
    }

    public <T> T get(String key, T def) {
        return attributeContainer.get(key, def);
    }

    @Nullable
    public <T> T get(ItemAttribute attribute) {
        return attributeContainer.get(attribute.getKey());
    }

    public <T> T get(ItemAttribute attribute, T def) {
        return attributeContainer.get(attribute.getKey(), def);
    }

    public <T> Set<T> getSet(Class<T> clazz) {
        return attributeContainer.getSet(clazz);
    }

    @Nullable
    public <V> Attribute<V> getAttribute(String key) {
        return attributeContainer.getAttribute(key);
    }

    public <V> Attribute<V> getAttribute(String key, Attribute<V> def) {
        return attributeContainer.getAttribute(key, def);
    }

    @Nullable
    public <V> Attribute<V> getAttribute(ItemAttribute attribute) {
        return attributeContainer.getAttribute(attribute.getKey());
    }

    public <V> Attribute<V> getAttribute(ItemAttribute attribute, Attribute<V> def) {
        return attributeContainer.getAttribute(attribute.getKey(), def);
    }

    public <V> Set<Attribute<V>> getAttributes(Class<Attribute<V>> clazz) {
        return attributeContainer.getAttributes(clazz);
    }

    @Nullable
    public <V> V getValue(String key) {
        return attributeContainer.getValue(key);
    }

    public <V> V getValue(String key, V def) {
        return attributeContainer.getValue(key, def);
    }

    @Nullable
    public <V> V getValue(ItemAttribute attribute) {
        return attributeContainer.getValue(attribute.getKey());
    }

    public <V> V getValue(ItemAttribute attribute, V def) {
        return attributeContainer.getValue(attribute.getKey(), def);
    }

    public <V> Set<V> getValues(Class<V> clazz) {
        return attributeContainer.getValues(clazz);
    }

    public Map<String, Attribute<?>> getAttributes() {
        return attributeContainer.getAttributes();
    }

    public AttributeMap getAttributeMap() {
        return attributeContainer.getAttributeMap();
    }

    public AttributeContainer getAttributeContainer() {
        return attributeContainer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {

        protected final AttributeContainerBuilder attributeContainerBuilder;

        public Builder() {
            this.attributeContainerBuilder = new ImmutableAttributeContainer(ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()).toBuilder();
        }

        public Builder(AttributeContainer attributeContainer) {
            this.attributeContainerBuilder = attributeContainer.toBuilder();
        }

        public Builder(ItemConfigurable itemConfigurable) {
            this.attributeContainerBuilder = itemConfigurable.getAttributeContainer().toBuilder();
        }

        public Builder ignore(ItemAttribute... ignore) {
            for (ItemAttribute ignoreAttribute : ignore)
                attributeContainerBuilder.ignore(ignoreAttribute.getKey());
            return this;
        }

        public Builder ignore(String... ignore) {
            attributeContainerBuilder.ignore(ignore);
            return this;
        }

        public Builder with(String key, AttributeFactory factory) {
            attributeContainerBuilder.with(key, factory);
            return this;
        }

        public Builder with(Map<String, AttributeFactory> keyMap) {
            attributeContainerBuilder.with(keyMap);
            return this;
        }

        public Builder with(Attribute<?> attribute) {
            attributeContainerBuilder.with(attribute);
            return this;
        }

        public Builder with(AttributeContainer container) {
            attributeContainerBuilder.with(container);
            return this;
        }

        public Builder copy(AttributeContainer container) {
            attributeContainerBuilder.copy(container);
            return this;
        }

        public Builder copy(ItemConfigurable itemConfigurable) {
            attributeContainerBuilder.copy(itemConfigurable.getAttributeContainer());
            return this;
        }

        public ItemConfigurable build() {
            return new ItemConfigurable(attributeContainerBuilder.buildImmutable());
        }

    }

}
