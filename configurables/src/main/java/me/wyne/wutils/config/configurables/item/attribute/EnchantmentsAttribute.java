package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.CompositeAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class EnchantmentsAttribute extends CompositeAttribute<EnchantmentAttribute> implements MetaAttribute {

    public EnchantmentsAttribute(String key, Map<String, EnchantmentAttribute> enchantments) {
        super(key, enchantments);
    }

    public EnchantmentsAttribute(String key, ConfigurationSection config) {
        super(key, config, new EnchantmentAttribute.Factory());
    }

    public EnchantmentsAttribute(Map<String, EnchantmentAttribute> enchantments) {
        super(ItemAttribute.ENCHANTMENTS.getKey(), enchantments);
    }

    public EnchantmentsAttribute(ConfigurationSection config) {
        super(ItemAttribute.ENCHANTMENTS.getKey(), config, new EnchantmentAttribute.Factory());
    }

    @Override
    public void apply(ItemMeta meta) {
        getValue().values().forEach(attribute -> attribute.apply(meta));
    }

}

