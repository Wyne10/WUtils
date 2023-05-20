package me.wyne.wutils.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public interface ConfigParameter {

    void getValue(@NotNull final FileConfiguration config, @NotNull final String path);

}
