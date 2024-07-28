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

    public void setConfigGenerator(File configFile, File defaultConfigFile)
    {
        configGenerator = new ConfigGenerator(configFile, defaultConfigFile);
    }

    public void setConfigGenerator(JavaPlugin plugin, String configPath)
    {
        File defaultConfig = new File(plugin.getDataFolder(), "defaults/config.yml");
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(configPath), defaultConfig);
        } catch (IOException e) {
            Log.global.exception("An exception occurred while trying to load default config for WUtils config", e);
        }
        setConfigGenerator(new File(plugin.getDataFolder(), configPath), defaultConfig);
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
                        if (configField.field().get(configField.holder()) != null && Configurable.class.isAssignableFrom(configField.field().get(configField.holder()).getClass()))
                            ((Configurable)configField.field().get(configField.holder())).fromConfig(config.get(configField.path()));
                        else
                            configField.field().set(configField.holder(), configField.field().getType() == String.class ? String.valueOf(config.get(configField.path())) : config.get(configField.path()));
                    } catch (IllegalAccessException e) {
                        Log.global.exception("An exception occurred while trying to reload WUtils config", e);
                    }
                });
        Log.global.info("Reloaded WUtils config");
    }

    public void generateConfig(String version, boolean backup, Map<String, String> replaceVars, List<String> deleteProps)
    {
        if (configGenerator == null)
        {
            Log.global.warn("Trying to generate config, but configGenerator is null");
            return;
        }

        replaceVars.put("version", version);
        deleteProps.add("config-version");
        configGenerator.writeVersion(version);
        configGenerator.writeConfigSections(ConfigFieldParser.getConfigSections(registeredConfigFields));
        configGenerator.generateConfig(backup, replaceVars, deleteProps);
    }

    public void generateConfig(String version)
    {
        generateConfig(version, true, new HashMap<>(), new ArrayList<>());
    }
}
