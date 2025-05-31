package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorColorAttribute extends ConfigurableAttribute<Color> implements MetaAttribute {

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

    public static final class Factory implements AttributeFactory {
        @Override
        public ArmorColorAttribute create(String key, ConfigurationSection config) {
            return new ArmorColorAttribute(key, Color.fromRGB(config.getInt(key, 0)));
        }
    }

}
