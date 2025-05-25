package me.wyne.wutils.config.configurables.item;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.item.attribute.*;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ItemConfig implements CompositeConfigurable {

    private final static AttributeMap ATTRIBUTE_MAP = new AttributeMap(new LinkedHashMap<>());

    static {
        ATTRIBUTE_MAP.put("name", (key, config) -> new NameAttribute(config.getString(key)));
        ATTRIBUTE_MAP.put("lore", (key, config) -> new LoreAttribute(config.getStringList(key)));
        ATTRIBUTE_MAP.put("flags", (key, config) -> {
            List<ItemFlag> flags = config.getStringList(key).stream()
                    .map(s -> {
                        try {
                            return ItemFlag.valueOf(s);
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return new FlagsAttribute(flags.toArray(ItemFlag[]::new));
        });
        ATTRIBUTE_MAP.put("skull", (key, config) -> {
            UUID uuid = Bukkit.getPlayerUniqueId(config.getString(key));
            return new SkullAttribute(Bukkit.getOfflinePlayer(uuid));
        });
        ATTRIBUTE_MAP.put("skull64", (key, config) -> new Skull64Attribute(config.getString(key)));
        ATTRIBUTE_MAP.put("skullPlayer", (key, config) -> new SkullPlayerAttribute(config.getBoolean(key, false)));
        ATTRIBUTE_MAP.put("unbreakable", (key, config) -> new UnbreakableAttribute(config.getBoolean(key, false)));
        ATTRIBUTE_MAP.put("enchantment", new EnchantmentAttribute.Factory());
        ATTRIBUTE_MAP.put("enchantments", (key, config) -> new CompositeAttribute(key, config, new EnchantmentAttribute.Factory()));
        ATTRIBUTE_MAP.put("attribute", new GenericAttribute.Factory());
        ATTRIBUTE_MAP.put("attributes", (key, config) -> new CompositeAttribute(key, config, new GenericAttribute.Factory()));
        ATTRIBUTE_MAP.put("glow", (key, config) -> new GlowAttribute(config.getBoolean(key, false)));
        ATTRIBUTE_MAP.put("durability", (key, config) -> new DurabilityAttribute(config.getInt(key, 1)));
        ATTRIBUTE_MAP.put("damage", (key, config) -> new DamageAttribute(config.getInt(key, 0)));
        ATTRIBUTE_MAP.put("model", (key, config) -> new CustomModelDataAttribute(config.contains(key) ? config.getInt(key) : null));
        ATTRIBUTE_MAP.put("repairCost", (key, config) -> new RepairCostAttribute(config.getInt(key, 1)));
        ATTRIBUTE_MAP.put("potionColor", (key, config) -> new PotionColorAttribute(Color.fromRGB(config.getInt(key, 0))));
        ATTRIBUTE_MAP.put("potionType", (key, config) -> {
            try {
                return new PotionTypeAttribute(PotionType.valueOf(config.getString(key, "WATER")));
            } catch (IllegalArgumentException e) {
                return new PotionTypeAttribute(PotionType.WATER);
            }
        });
        ATTRIBUTE_MAP.put("potionModifier", (key, config) -> {
            try {
                return new PotionModifierAttribute(PotionModifierAttribute.PotionModifier.valueOf(config.getString(key, "NONE")));
            } catch (IllegalArgumentException e) {
                return new PotionModifierAttribute(PotionModifierAttribute.PotionModifier.NONE);
            }
        });
        ATTRIBUTE_MAP.put("potionEffect", new PotionEffectAttribute.Factory());
        ATTRIBUTE_MAP.put("potionEffects", (key, config) -> new CompositeAttribute(key, config, new PotionEffectAttribute.Factory()));
        ATTRIBUTE_MAP.put("armorColor", (key, config) -> new ArmorColorAttribute(Color.fromRGB(config.getInt(key, 0))));
        ATTRIBUTE_MAP.put("print", (key, config) -> new PrintAttribute(config.getString(key)));
    }

    private final Map<String, Attribute<?>> itemAttributes;

    public ItemConfig() {
        itemAttributes = new LinkedHashMap<>();
    }

    public ItemConfig(Map<String, Attribute<?>> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    @SuppressWarnings("unchecked")
    public <T> Attribute<T> getAttribute(String key) {
        return (Attribute<T>) itemAttributes.get(key);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        itemAttributes.values().stream()
                .filter(attribute -> !(attribute instanceof GuiActionAttribute))
                .forEach(attribute -> builder.append(attribute.toConfig(depth, configEntry)));
        return builder.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        itemAttributes.clear();
        itemAttributes.putAll(ATTRIBUTE_MAP.createAllMap((ConfigurationSection) configObject));
    }

    public ItemStack createItemStack(TextReplacement... textReplacements) {
        var itemStack = new ItemStack(Material.STONE);
        itemAttributes.values().stream()
                .filter(attribute -> attribute instanceof ContextPlaceholderAttribute)
                .map(attribute -> (ContextPlaceholderAttribute) attribute)
                .forEach(attribute -> attribute.apply(itemStack, textReplacements));
        itemAttributes.values().stream()
                .filter(attribute -> !(attribute instanceof ContextPlaceholderAttribute))
                .forEach(attribute -> attribute.apply(itemStack));
        return itemStack;
    }

    public ItemStack createItemStack(Player player, TextReplacement... textReplacements) {
        var itemStack = new ItemStack(Material.STONE);
        itemAttributes.values().stream()
                .filter(attribute -> attribute instanceof ContextPlaceholderAttribute)
                .map(attribute -> (ContextPlaceholderAttribute) attribute)
                .forEach(attribute -> attribute.apply(itemStack, player, textReplacements));
        itemAttributes.values().stream()
                .filter(attribute -> !(attribute instanceof ContextPlaceholderAttribute))
                .forEach(attribute -> {
                    if (attribute instanceof PlayerAwareAttribute playerAwareAttribute)
                        playerAwareAttribute.apply(itemStack, player);
                    else
                        attribute.apply(itemStack);
                });
        return itemStack;
    }

    public ItemStack createItemStack(ComponentReplacement... componentReplacements) {
        var itemStack = new ItemStack(Material.STONE);
        itemAttributes.values().stream()
                .filter(attribute -> attribute instanceof ContextPlaceholderAttribute)
                .map(attribute -> (ContextPlaceholderAttribute) attribute)
                .forEach(attribute -> attribute.apply(itemStack, componentReplacements));
        itemAttributes.values().stream()
                .filter(attribute -> !(attribute instanceof ContextPlaceholderAttribute))
                .forEach(attribute -> attribute.apply(itemStack));
        return itemStack;
    }

    public ItemStack createItemStack(Player player, ComponentReplacement... componentReplacements) {
        var itemStack = new ItemStack(Material.STONE);
        itemAttributes.values().stream()
                .filter(attribute -> attribute instanceof ContextPlaceholderAttribute)
                .map(attribute -> (ContextPlaceholderAttribute) attribute)
                .forEach(attribute -> attribute.apply(itemStack, player, componentReplacements));
        itemAttributes.values().stream()
                .filter(attribute -> !(attribute instanceof ContextPlaceholderAttribute))
                .forEach(attribute -> {
                    if (attribute instanceof PlayerAwareAttribute playerAwareAttribute)
                        playerAwareAttribute.apply(itemStack, player);
                    else
                        attribute.apply(itemStack);
                });
        return itemStack;
    }

    public GuiItem createGuiItem(TextReplacement... textReplacements) {
        var itemStack = createItemStack(textReplacements);
        var guiActions = itemAttributes.values().stream()
                .filter(attribute -> attribute instanceof ClickEventAttribute)
                .map(attribute -> (ClickEventAttribute) attribute)
                .toList();
        return ItemBuilder.from(itemStack)
                .asGuiItem(e -> guiActions.forEach(action -> action.apply(e)));
    }

    public GuiItem createGuiItem(Player player, TextReplacement... textReplacements) {
        var itemStack = createItemStack(player, textReplacements);
        var guiActions = itemAttributes.values().stream()
                .filter(attribute -> attribute instanceof ClickEventAttribute)
                .map(attribute -> (ClickEventAttribute) attribute)
                .toList();
        return ItemBuilder.from(itemStack)
                .asGuiItem(e -> guiActions.forEach(action -> action.apply(e)));
    }

    public GuiItem createGuiItem(ComponentReplacement... componentReplacements) {
        var itemStack = createItemStack(componentReplacements);
        var guiActions = itemAttributes.values().stream()
                .filter(attribute -> attribute instanceof ClickEventAttribute)
                .map(attribute -> (ClickEventAttribute) attribute)
                .toList();
        return ItemBuilder.from(itemStack)
                .asGuiItem(e -> guiActions.forEach(action -> action.apply(e)));
    }

    public GuiItem createGuiItem(Player player, ComponentReplacement... componentReplacements) {
        var itemStack = createItemStack(player, componentReplacements);
        var guiActions = itemAttributes.values().stream()
                .filter(attribute -> attribute instanceof ClickEventAttribute)
                .map(attribute -> (ClickEventAttribute) attribute)
                .toList();
        return ItemBuilder.from(itemStack)
                .asGuiItem(e -> guiActions.forEach(action -> action.apply(e)));
    }

    public static ItemConfig fromConfig(ConfigurationSection config) {
        return new ItemConfig(ATTRIBUTE_MAP.createAllMap(config));
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {

        private final Map<String, Attribute<?>> itemAttributes;

        public Builder() {
            itemAttributes = new LinkedHashMap<>();
        }

        public Builder(ItemConfig itemConfig) {
            this.itemAttributes = new LinkedHashMap<>(itemConfig.itemAttributes);
        }

        public Builder ignore(ItemAttribute... ignore) {
            for (ItemAttribute ignoreAttribute : ignore)
                itemAttributes.remove(ignoreAttribute.getKey());
            return this;
        }

        public Builder ignore(String... ignore) {
            for (String ignoreKey : ignore)
                itemAttributes.remove(ignoreKey);
            return this;
        }

        public Builder with(Attribute<?> attribute) {
            itemAttributes.put(attribute.getKey(), attribute);
            return this;
        }

        public Builder with(String key, Attribute<?> attribute) {
            itemAttributes.put(key, attribute);
            return this;
        }

        public Builder with(ItemAttribute key, Attribute<?> attribute) {
            itemAttributes.put(key.getKey(), attribute);
            return this;
        }

        public ItemConfig build() {
            return new ItemConfig(itemAttributes);
        }

    }

}
