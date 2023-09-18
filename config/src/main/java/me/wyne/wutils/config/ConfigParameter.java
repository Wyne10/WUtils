package me.wyne.wutils.config;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigParameter<TRet> {

    TRet getValue(FileConfiguration config, String path);

}
