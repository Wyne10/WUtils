package me.wyne.wutils.config.configurables.invui.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.invui.InvUiAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class StructureKeyAttribute extends ConfigurableAttribute<Character> {

    public StructureKeyAttribute(String key, char value) {
        super(key, value);
    }

    public StructureKeyAttribute(char value) {
        super(InvUiAttribute.KEY.getKey(), value);
    }

    public static final class Factory implements AttributeFactory<StructureKeyAttribute> {
        @Override
        public StructureKeyAttribute create(String key, ConfigurationSection config) {
            return new StructureKeyAttribute(key, config.getString(key, ".").charAt(0));
        }
    }

}
