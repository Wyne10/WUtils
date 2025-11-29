package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

public interface CompositeAttributeFactory<T extends Attribute<?>> extends AttributeFactory<T> {
    @Override
    default T create(String key, ConfigurationSection config) {
        if (config.isConfigurationSection(key))
            return fromSection(key, config.getConfigurationSection(key));
        else
            return fromString(key, config.getString(key), config);
    }

    T fromSection(String key, ConfigurationSection section);

    T fromString(String key, String string, ConfigurationSection config);
}
