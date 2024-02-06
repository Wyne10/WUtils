package me.wyne.wutils.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.javatuples.Pair;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class Config {

    public static final Config global = new Config();

    private ConfigGenerator configGenerator;
    private final Map<String, Set<ConfigField>> registeredConfigFields = new LinkedHashMap<>();

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
        for(Field field  : object.getClass().getDeclaredFields())
        {
            if (!field.isAnnotationPresent(ConfigEntry.class))
                continue;
            Pair<String, ConfigField> sectionedConfigField = ConfigFieldParser.getSectionedConfigField(object, field);
            registerConfigField(sectionedConfigField.getValue0(), sectionedConfigField.getValue1());
        }
    }

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
                        throw new RuntimeException(e); // TODO Add logging
                    }
                });
    }

    public void generateConfig(String version)
    {
        if (configGenerator == null)
            return; // TODO Add logging

        configGenerator.writeVersion(version);
        configGenerator.writeConfigSections(ConfigFieldParser.getConfigSections(registeredConfigFields));
        configGenerator.generateConfig();
    }
}
