package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorColorAttribute extends AttributeBase<Color> implements MetaAttribute, ConfigurableAttribute<Color> {

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

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().asRGB()).buildNoSpace();
    }

}
