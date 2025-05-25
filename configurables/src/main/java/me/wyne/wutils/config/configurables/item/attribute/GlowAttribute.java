package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class GlowAttribute extends AttributeBase<Boolean> implements MetaAttribute, ConfigurableAttribute<Boolean> {

    public GlowAttribute(String key, Boolean value) {
        super(key, value);
    }

    public GlowAttribute(Boolean value) {
        super(ItemAttribute.GLOW.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (meta.hasEnchants()) return;
        if (!getValue()) return;
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            return new GlowAttribute(config.getBoolean(key, false));
        }
    }

}
