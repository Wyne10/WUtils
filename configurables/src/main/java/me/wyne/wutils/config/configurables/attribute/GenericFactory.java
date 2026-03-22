package me.wyne.wutils.config.configurables.attribute;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GenericFactory<T> {
    @NotNull T create(@NotNull String key, @NotNull ConfigurationSection config);
}
