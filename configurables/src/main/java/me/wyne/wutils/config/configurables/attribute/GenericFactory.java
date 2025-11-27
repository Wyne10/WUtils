package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;

public interface GenericFactory<T> {
    T create(String key, ConfigurationSection config);
}
