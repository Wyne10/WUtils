package me.wyne.wutils.config;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigParameter{

    Object getValue(FileConfiguration config, String path);

}
