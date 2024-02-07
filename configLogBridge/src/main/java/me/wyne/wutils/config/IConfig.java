package me.wyne.wutils.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public interface IConfig {

    void setConfigGenerator(File configFile, FileConfiguration config);
    void registerConfigObject(Object object);
    void registerConfigField(String section, ConfigField field);
    void reloadConfig(FileConfiguration config);
    void generateConfig(String version);

}
