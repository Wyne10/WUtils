package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.config.configurables.item.attribute.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.stream.Collectors;

public class ItemConfig {

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
        ATTRIBUTE_MAP.put("skullPlayer", (key, config) -> new SkullPlayerAttribute());
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

    public String toConfig(int depth) {
        StringBuilder builder = new StringBuilder();
        itemAttributes.values().forEach(attribute -> builder.append(" ".repeat(depth * 2)).append(attribute.toConfig()).append('\n'));
        return builder.toString();
    }

    public ItemStack createItemStack() {
        var itemStack = new ItemStack(Material.STONE);
        itemAttributes.values().forEach(attribute -> attribute.apply(itemStack));
        return itemStack;
    }

    public ItemStack createItemStack(Player player) {
        var itemStack = new ItemStack(Material.STONE);
        itemAttributes.values().forEach(attribute -> {
            if (attribute instanceof PlayerAwareAttribute<?> playerAwareAttribute)
                playerAwareAttribute.apply(itemStack, player);
            else
                attribute.apply(itemStack);
        });
        return itemStack;
    }

    public ItemStack createItemStack(Attribute<?>... attributes) {
        var itemStack = createItemStack();
        for (Attribute<?> attribute : attributes)
            attribute.apply(itemStack);
        return itemStack;
    }

    public ItemStack createItemStack(Player player, Attribute<?>... attributes) {
        var itemStack = createItemStack(player);
        for (Attribute<?> attribute : attributes)
            attribute.apply(itemStack);
        return itemStack;
    }

    public ItemStack createItemStack(ItemAttribute... ignoring) {
        Set<String> ignoreSet = Arrays.stream(ignoring).map(ItemAttribute::getKey).collect(Collectors.toSet());
        var itemStack = new ItemStack(Material.STONE);
        itemAttributes.keySet().stream()
                .filter(ignoreSet::contains)
                .forEach(key -> itemAttributes.get(key).apply(itemStack));
        return itemStack;
    }

    public ItemStack createItemStack(Player player, ItemAttribute... ignoring) {
        Set<String> ignoreSet = Arrays.stream(ignoring).map(ItemAttribute::getKey).collect(Collectors.toSet());
        var itemStack = new ItemStack(Material.STONE);
        itemAttributes.keySet().stream()
                .filter(ignoreSet::contains)
                .forEach(key -> {
                    if (itemAttributes.get(key) instanceof PlayerAwareAttribute<?> playerAwareAttribute)
                        playerAwareAttribute.apply(itemStack, player);
                    else
                        itemAttributes.get(key).apply(itemStack);
                });
        return itemStack;
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

        public ItemConfig build() {
            return new ItemConfig(itemAttributes);
        }

    }

}
