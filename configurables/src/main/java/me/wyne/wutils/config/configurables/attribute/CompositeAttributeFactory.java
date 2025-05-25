package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

public interface CompositeAttributeFactory extends AttributeFactory {
    @Override
    default ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
        if (config.isConfigurationSection(key))
            return fromSection(key, config.getConfigurationSection(key));
        else
            return fromString(key, config.getString(key));
    }

    ConfigurableAttribute<?> fromSection(String key, ConfigurationSection section);

    ConfigurableAttribute<?> fromString(String key, String string);
}
