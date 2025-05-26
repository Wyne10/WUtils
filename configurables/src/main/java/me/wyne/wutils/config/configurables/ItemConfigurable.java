package me.wyne.wutils.config.configurables;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ItemConfigurable implements CompositeConfigurable {

    public final static AttributeMap ITEM_ATTRIBUTE_MAP = new AttributeMap(new LinkedHashMap<>());
    
    static {
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.MATERIAL.getKey(), new MaterialAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.NAME.getKey(), new NameAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.LORE.getKey(), new LoreAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.FLAGS.getKey(), new FlagsAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SKULL.getKey(), new SkullAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SKULL64.getKey(), new Skull64Attribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SKULL_PLAYER.getKey(), new SkullPlayerAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.UNBREAKABLE.getKey(), new UnbreakableAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ENCHANTMENT.getKey(), new EnchantmentAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ENCHANTMENTS.getKey(), (key, config) -> new CompositeAttribute(key, config, new EnchantmentAttribute.Factory()));
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ATTRIBUTE.getKey(), new GenericAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ATTRIBUTES.getKey(), (key, config) -> new CompositeAttribute(key, config, new GenericAttribute.Factory()));
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.GLOW.getKey(), new GlowAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.DURABILITY.getKey(), new DurabilityAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.DAMAGE.getKey(), new DamageAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.MODEL.getKey(), new CustomModelDataAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.REPAIR_COST.getKey(), new RepairCostAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_COLOR.getKey(), new PotionColorAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_TYPE.getKey(), new PotionTypeAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_MODIFIER.getKey(), new PotionModifierAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_EFFECT.getKey(), new PotionEffectAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.POTION_EFFECTS.getKey(), (key, config) -> new CompositeAttribute(key, config, new PotionEffectAttribute.Factory()));
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.ARMOR_COLOR.getKey(), new ArmorColorAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.PRINT.getKey(), new PrintAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SOUND.getKey(), new SoundAttribute.Factory());
        ITEM_ATTRIBUTE_MAP.put(ItemAttribute.SLOT.getKey(), new SlotAttribute.Factory());
    }
    
    private AttributeContainer attributeContainer;

    public ItemConfigurable() {
        attributeContainer = new AttributeContainer(ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>());
    }

    public ItemConfigurable(ConfigurationSection section) {
        super();
        fromConfig(section);
    }

    public ItemConfigurable(AttributeContainer attributeContainer) {
        this.attributeContainer = attributeContainer;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return attributeContainer.toConfig(depth, configEntry);
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        attributeContainer.fromConfig(configObject);
    }

    public ItemStack build(TextReplacement... textReplacements) {
        var itemStack = new ItemStack(Material.STONE);
        attributeContainer.getSet(ContextPlaceholderAttribute.class)
                .forEach(attribute -> attribute.apply(textReplacements));
        attributeContainer.getSet(ItemStackAttribute.class)
                .forEach(attribute -> attribute.apply(itemStack));
        return itemStack;
    }

    public ItemStack build(Player player, TextReplacement... textReplacements) {
        var itemStack = new ItemStack(Material.STONE);
        attributeContainer.getSet(ContextPlaceholderAttribute.class)
                .forEach(attribute -> attribute.apply(textReplacements));
        attributeContainer.getSet(PlayerAwareAttribute.class)
                .forEach(attribute -> attribute.apply(player));
        attributeContainer.getSet(ItemStackAttribute.class)
                .forEach(attribute -> attribute.apply(itemStack));
        return itemStack;
    }

    public ItemStack buildComponent(ComponentReplacement... componentReplacements) {
        var itemStack = new ItemStack(Material.STONE);
        attributeContainer.getSet(ContextPlaceholderAttribute.class)
                .forEach(attribute -> attribute.apply(componentReplacements));
        attributeContainer.getSet(ItemStackAttribute.class)
                .forEach(attribute -> attribute.apply(itemStack));
        return itemStack;
    }

    public ItemStack buildComponent(Player player, ComponentReplacement... componentReplacements) {
        var itemStack = new ItemStack(Material.STONE);
        attributeContainer.getSet(ContextPlaceholderAttribute.class)
                .forEach(attribute -> attribute.apply(componentReplacements));
        attributeContainer.getSet(PlayerAwareAttribute.class)
                .forEach(attribute -> attribute.apply(player));
        attributeContainer.getSet(ItemStackAttribute.class)
                .forEach(attribute -> attribute.apply(itemStack));
        return itemStack;
    }

    public GuiItem buildGuiItem(TextReplacement... textReplacements) {
        var itemStack = build(textReplacements);
        var actions = attributeContainer.getSet(ClickEventAttribute.class);
        return ItemBuilder.from(itemStack)
                .asGuiItem(e -> actions.forEach(attribute -> attribute.apply(e)));
    }

    public GuiItem buildGuiItem(Player player, TextReplacement... textReplacements) {
        var itemStack = build(player, textReplacements);
        var actions = attributeContainer.getSet(ClickEventAttribute.class);
        return ItemBuilder.from(itemStack)
                .asGuiItem(e -> actions.forEach(attribute -> attribute.apply(e)));

    }

    public GuiItem buildGuiItemComponent(ComponentReplacement... componentReplacements) {
        var itemStack = buildComponent(componentReplacements);
        var actions = attributeContainer.getSet(ClickEventAttribute.class);
        return ItemBuilder.from(itemStack)
                .asGuiItem(e -> actions.forEach(attribute -> attribute.apply(e)));
    }

    public GuiItem buildGuiItemComponent(Player player, ComponentReplacement... componentReplacements) {
        var itemStack = buildComponent(player, componentReplacements);
        var actions = attributeContainer.getSet(ClickEventAttribute.class);
        return ItemBuilder.from(itemStack)
                .asGuiItem(e -> actions.forEach(attribute -> attribute.apply(e)));
    }

    public ItemConfigurable ignore(ItemAttribute... ignore) {
        for (ItemAttribute ignoreAttribute : ignore)
            attributeContainer.ignore(ignoreAttribute.getKey());
        return this;
    }

    public ItemConfigurable ignore(String... ignore) {
        attributeContainer.ignore(ignore);
        return this;
    }

    public ItemConfigurable with(String key, AttributeFactory factory) {
        attributeContainer.with(key, factory);
        return this;
    }

    public ItemConfigurable with(Attribute<?> attribute) {
        attributeContainer.with(attribute);
        return this;
    }

    public <T> T get(String key) {
        return attributeContainer.get(key);
    }

    public <T> T get(ItemAttribute attribute) {
        return attributeContainer.get(attribute.getKey());
    }

    public <T> Set<T> getSet(Class<T> clazz) {
        return attributeContainer.getSet(clazz);
    }

    public <V> Attribute<V> getAttribute(String key) {
        return attributeContainer.getAttribute(key);
    }

    public <V> Attribute<V> getAttribute(ItemAttribute attribute) {
        return attributeContainer.getAttribute(attribute.getKey());
    }

    public <V> Set<Attribute<V>> getAttributes(Class<Attribute<V>> clazz) {
        return attributeContainer.getAttributes(clazz);
    }

    public Map<String, Attribute<?>> getAttributes() {
        return attributeContainer.getAttributes();
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

    public static final class Builder {

        private final AttributeContainer.Builder attributeContainerBuilder;

        public Builder() {
            this.attributeContainerBuilder = new AttributeContainer(ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()).toBuilder();
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

        public Builder with(Attribute<?> attribute) {
            attributeContainerBuilder.with(attribute);
            return this;
        }

        public ItemConfigurable build() {
            return new ItemConfigurable(attributeContainerBuilder.build());
        }

    }

}
