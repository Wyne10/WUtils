package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class SlotAttribute extends AttributeBase<Integer> implements ConfigurableAttribute<Integer> {

    public SlotAttribute(String key, Integer value) {
        super(key, value);
    }

    public SlotAttribute(Integer value) {
        super(ItemAttribute.SLOT.getKey(), value);
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            return new SlotAttribute(key, config.getInt(key, 0));
        }
    }

}
