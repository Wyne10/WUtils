package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import org.bukkit.configuration.ConfigurationSection;

public class RootAttribute extends AttributeBase<ConfigurationSection> {

    public RootAttribute(String key, ConfigurationSection value) {
        super(key, value);
    }

    public static final class Factory implements AttributeFactory<RootAttribute> {
        @Override
        public RootAttribute create(String key, ConfigurationSection config) {
            return new RootAttribute(key, config);
        }
    }

}
