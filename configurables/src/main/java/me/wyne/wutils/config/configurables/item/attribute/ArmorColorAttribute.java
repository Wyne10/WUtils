package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.common.ColorAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorColorAttribute extends ColorAttribute implements MetaAttribute {

    public ArmorColorAttribute(String key, Color value) {
        super(key, value);
    }

    public ArmorColorAttribute(Color value) {
        super(ItemAttribute.ARMOR_COLOR.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof LeatherArmorMeta)) return;
        ((LeatherArmorMeta)meta).setColor(getValue());
    }

    public static final class Factory implements AttributeFactory<ArmorColorAttribute> {
        @Override
        public ArmorColorAttribute create(String key, ConfigurationSection config) {
            return new ArmorColorAttribute(key, new ColorAttribute.Factory().create(key, config).getValue());
        }
    }

}
