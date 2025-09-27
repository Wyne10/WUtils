package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.common.Args;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentAttribute extends ConfigurableAttribute<EnchantmentAttribute.EnchantmentData> implements MetaAttribute {

    public EnchantmentAttribute(String key, EnchantmentData value) {
        super(key, value);
    }

    public EnchantmentAttribute(EnchantmentData value) {
        super(ItemAttribute.ENCHANTMENT.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.addEnchant(getValue().enchantment(), getValue().level(), true);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().enchantment().getKey() + " " + getValue().level()).buildNoSpace();
    }

    public record EnchantmentData(Enchantment enchantment, int level) {}

    public static final class Factory implements CompositeAttributeFactory {
        @Override
        public EnchantmentAttribute fromSection(String key, ConfigurationSection section) {
            return new EnchantmentAttribute(
                    key,
                    new EnchantmentData(
                            Enchantment.getByKey(NamespacedKey.fromString(section.getString("enchantment", "lure"))),
                            section.getInt("level", 1)
                    )
            );
        }

        @Override
        public EnchantmentAttribute fromString(String key, String string) {
            var args = new Args(string, " ");
            return new EnchantmentAttribute(
                    key,
                    new EnchantmentData(
                            Enchantment.getByKey(NamespacedKey.fromString(args.get(0, "lure"))),
                            Integer.parseInt(args.get(1, "1"))
                    )
            );
        }
    }

}

