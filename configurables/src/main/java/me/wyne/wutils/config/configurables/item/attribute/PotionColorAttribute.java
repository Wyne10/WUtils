package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.common.ColorAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class PotionColorAttribute extends ColorAttribute implements MetaAttribute {

    public PotionColorAttribute(String key, Color value) {
        super(key, value);
    }

    public PotionColorAttribute(Color value) {
        super(ItemAttribute.POTION_COLOR.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof PotionMeta)) return;
        ((PotionMeta)meta).setColor(getValue());
    }

    public static final class Factory implements AttributeFactory<PotionColorAttribute> {
        @Override
        public PotionColorAttribute create(String key, ConfigurationSection config) {
            return new PotionColorAttribute(key, new ColorAttribute.Factory().create(key, config).getValue());
        }
    }

}
