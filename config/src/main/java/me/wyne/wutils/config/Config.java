package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.javatuples.Pair;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class Config implements IConfig {

    public static final IConfig global = new Config();

    private ConfigGenerator configGenerator;
    private final Map<String, Set<ConfigField>> registeredConfigFields = new LinkedHashMap<>();

    public Config(File configFile, FileConfiguration config)
    {
        setConfigGenerator(configFile, config);
    }

    protected Config() {
        registerConfigObject(this);
    }

    @Override
    public void setConfigGenerator(File configFile, FileConfiguration config)
    {
        configGenerator = new ConfigGenerator(configFile, config);
    }

    @Override
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

    @Override
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

    @Override
    public void generateConfig(String version)
    {
        if (configGenerator == null)
        {
            Log.global.warn("Trying to generate config but configGenerator is null");
            return;
        }

        configGenerator.writeVersion(version);
        configGenerator.writeConfigSections(ConfigFieldParser.getConfigSections(registeredConfigFields));
        configGenerator.generateConfig();
    }
}
