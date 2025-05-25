package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

public interface AttributeFactory {
    ConfigurableAttribute<?> create(String key, ConfigurationSection config);
}
