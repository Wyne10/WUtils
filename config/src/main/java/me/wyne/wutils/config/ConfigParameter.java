package me.wyne.wutils.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * If {@link ConfigField} annotation is present on a field with a type inherited from this interface, then getValue(FileConfiguration, String) will be called on config object reload.
 */
public interface ConfigParameter {

    Object getValue(@NotNull final FileConfiguration config, @NotNull final String path);

}
