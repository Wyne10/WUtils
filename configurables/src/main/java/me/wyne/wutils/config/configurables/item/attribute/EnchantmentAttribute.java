package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.common.Args;
import me.wyne.wutils.config.configurables.item.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentAttribute extends MetaAttribute<EnchantmentAttribute.EnchantmentData> {

    public EnchantmentAttribute(EnchantmentData value) {
        super(ItemAttribute.ENCHANTMENT.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.addEnchant(getValue().enchantment(), getValue().level(), true);
    }

    @Override
    public String toString() {
        return getValue().enchantment().getKey() + " " + getValue().level();
    }

    public record EnchantmentData(Enchantment enchantment, int level) {}

    public static class Factory implements CompositeAttributeFactory {
        @Override
        public EnchantmentAttribute fromSection(String key, ConfigurationSection section) {
            return new EnchantmentAttribute(
                    new EnchantmentData(
                            Enchantment.getByKey(NamespacedKey.fromString(section.getString("enchantment", "lure"))),
                            section.getInt("level", 1)
                    )
            );
        }

        @Override
        public EnchantmentAttribute fromString(String key, String string) {
            var args = new Args(string, ":");
            return new EnchantmentAttribute(
                    new EnchantmentData(
                            Enchantment.getByKey(NamespacedKey.fromString(args.get(0, "lure"))),
                            Integer.parseInt(args.get(1, "1"))
                    )
            );
        }
    }

}

