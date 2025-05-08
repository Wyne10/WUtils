package me.wyne.wutils.config.configurables.item;

import org.bukkit.configuration.ConfigurationSection;

public interface CompositeAttributeFactory extends AttributeFactory {
    @Override
    default Attribute<?> create(String key, ConfigurationSection config) {
        if (config.isConfigurationSection(key))
            return fromSection(key, config.getConfigurationSection(key));
        else
            return fromString(key, config.getString(key));
    }

    Attribute<?> fromSection(String key, ConfigurationSection section);

    Attribute<?> fromString(String key, String string);
}
