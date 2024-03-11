package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.javatuples.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class Config implements ConfigFieldRegistry {

    public static final Config global = new Config();

    private ConfigGenerator configGenerator;
    private final Map<String, Set<ConfigField>> registeredConfigFields = new LinkedHashMap<>();

    protected Config() {
        registerConfigObject(this);
    }

    public void setConfigGenerator(File configFile, File defaultConfigFile, FileConfiguration existingConfig)
    {
        configGenerator = new ConfigGenerator(configFile, defaultConfigFile, existingConfig);
    }

    public void setConfigGenerator(JavaPlugin plugin, String defaultConfigPath, FileConfiguration existingConfig)
    {
        File defaultConfig = new File(plugin.getDataFolder(), "defaults/config.yml");
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(defaultConfigPath), defaultConfig);
        } catch (IOException e) {
            Log.global.exception("An exception occurred while trying to load default config", e);
        }
        setConfigGenerator(new File(plugin.getDataFolder(), defaultConfigPath), defaultConfig, existingConfig);
    }

    public void registerConfigObject(Object object)
    {
        for(Field field : object.getClass().getDeclaredFields())
        {
            if (!field.isAnnotationPresent(ConfigEntry.class))
                continue;
            Pair<String, ConfigField> sectionedConfigField = ConfigFieldParser.getSectionedConfigField(object, field);
            registerConfigField(sectionedConfigField.getValue0(), sectionedConfigField.getValue1());
        }
    }

    @Override
    public void registerConfigField(String section, ConfigField field) {
        if (!registeredConfigFields.containsKey(section) || registeredConfigFields.get(section) == null)
            registeredConfigFields.put(section, new LinkedHashSet<>());

        registeredConfigFields.get(section).add(field);
    }

    public void reloadConfig(FileConfiguration config) {
        registeredConfigFields.values()
                .stream()
                .flatMap(Collection::stream)
                .forEachOrdered(configField -> {
                    configField.field().setAccessible(true);
                    try {
                        configField.field().set(configField.holder(), configField.field().getType() == String.class ? String.valueOf(config.get(configField.path())) : config.get(configField.path()));
                    } catch (IllegalAccessException e) {
                        Log.global.exception("An exception occurred while trying to reload WUtils config", e);
                    }
                });
        Log.global.info("Reloaded WUtils config");
    }

    public void generateConfig(JavaPlugin plugin, String version)
    {
        if (configGenerator == null)
        {
            Log.global.warn("Trying to generate config but configGenerator is null");
            return;
        }

        configGenerator.writeVersion(version);
        configGenerator.writeConfigSections(ConfigFieldParser.getConfigSections(registeredConfigFields));
        configGenerator.generateConfig(plugin);
    }
}
