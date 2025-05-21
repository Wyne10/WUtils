package me.wyne.wutils.config.configurables.item;

import org.bukkit.configuration.ConfigurationSection;

public interface AttributeFactory {
    Attribute<?> create(String key, ConfigurationSection config);
}
