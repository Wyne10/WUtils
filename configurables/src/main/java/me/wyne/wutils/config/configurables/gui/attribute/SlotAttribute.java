package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class SlotAttribute extends ConfigurableAttribute<Integer> {

    public SlotAttribute(String key, Integer value) {
        super(key, value);
    }

    public SlotAttribute(Integer value) {
        super(GuiItemAttribute.SLOT.getKey(), value);
    }

    public static final class Factory implements AttributeFactory<SlotAttribute> {
        @Override
        public SlotAttribute create(String key, ConfigurationSection config) {
            return new SlotAttribute(key, config.getInt(key, 0));
        }
    }

}
