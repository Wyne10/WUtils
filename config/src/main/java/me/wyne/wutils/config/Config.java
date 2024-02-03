package me.wyne.wutils.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class Config {

    public static final Config global = new Config();

    private ConfigEntryParser configEntryParser;
    private ConfigGenerator configGenerator;
    private final Set<Object> registeredConfigObjects = new HashSet<>();

    public Config(File configFile, FileConfiguration config)
    {
        setConfigGenerator(configFile, config);
    }

    protected Config() {
        registerConfigObject(this);
    }

    public void setConfigGenerator(File configFile, FileConfiguration config)
    {
        configGenerator = new ConfigGenerator(configFile, config);
    }

    public void registerConfigObject(Object object)
    {
        registeredConfigObjects.add(object);
    }

    public void reloadConfigObjects(FileConfiguration config) {
        for (Object object : registeredConfigObjects)
        {
            for(Field field  : object.getClass().getDeclaredFields())
            {
                if (!field.isAnnotationPresent(ConfigEntry.class))
                    continue;

                field.setAccessible(true);
                String path = field.getAnnotation(ConfigEntry.class).path().isEmpty() ? field.getName() : field.getAnnotation(ConfigEntry.class).path();
                try {
                    field.set(object, field.getType() == String.class ? String.valueOf(config.get(path)) : config.get(path));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e); // TODO Add logging
                }
            }
        }
    }

    public void generateConfig(String version)
    {
        if (configGenerator == null)
            return; // TODO Add logging

        configEntryParser = new ConfigEntryParser(registeredConfigObjects);
        configGenerator.writeVersion(version);
        configGenerator.writeConfigSections(configEntryParser.getConfigSections());
        configGenerator.generateConfig();
    }
}
