package me.wyne.wutils.config.configurables.item;

import org.bukkit.configuration.ConfigurationSection;

public interface AttributeFactory {
    ConfigurableAttribute<?> create(String key, ConfigurationSection config);
}
