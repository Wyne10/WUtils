package me.wyne.wutils.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class Config {

    public static final Config global = new Config();

    private ConfigGenerator configGenerator;
    private final Map<String, Set<ConfigField>> registeredConfigFields = new LinkedHashMap<>();
    private final Set<Object> registeredConfigObjects = new LinkedHashSet<>();

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

    public void registerConfigField(String section, ConfigField field) {
        if (!registeredConfigFields.containsKey(section) || registeredConfigFields.get(section) == null)
            registeredConfigFields.put(section, new LinkedHashSet<>());

        registeredConfigFields.get(section).add(field);
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

        ConfigEntryParser configEntryParser = new ConfigEntryParser(registeredConfigFields, registeredConfigObjects);
        configGenerator.writeVersion(version);
        configGenerator.writeConfigSections(configEntryParser.getConfigSections());
        configGenerator.generateConfig();
    }
}
