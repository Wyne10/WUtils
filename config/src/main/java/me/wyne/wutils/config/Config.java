package me.wyne.wutils.config;

import me.wyne.wutils.config.configurable.Configurable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.javatuples.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Config implements ConfigFieldRegistry {

    public static final Config global = new Config();
    public final LogWrapper log = new LogWrapper();

    private ConfigGenerator configGenerator;
    /**
     * Key - Section
     */
    private final Map<String, Set<ConfigField>> registeredConfigFields = new LinkedHashMap<>();

    static {
        try {
            Configurator.setLevel(LogManager.getLogger("ru.vyarus"), Level.WARN);
        } catch (NoSuchMethodError ignored) {}
    }

    public void setConfigGenerator(File configFile, File defaultConfigFile)
    {
        configGenerator = new ConfigGenerator(configFile, defaultConfigFile, log);
    }

    public void setConfigGenerator(JavaPlugin plugin, String configPath)
    {
        File defaultConfig = new File(plugin.getDataFolder(), "defaults/config.yml");
        try {
            Files.copy(plugin.getResource(configPath), defaultConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.exception("An exception occurred trying to load default config for WUtils config", e);
        }
        setConfigGenerator(new File(plugin.getDataFolder(), configPath), defaultConfig);
    }

    public void registerConfigObject(Object object)
    {
        for(Field field : object.getClass().getDeclaredFields())
        {
            if (!field.isAnnotationPresent(ConfigEntry.class))
                continue;
            Pair<String, ConfigField> sectionedConfigField = ConfigFieldParser.getSectionedConfigField(object, field, log);
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
                        if (!config.contains(configField.path()))
                            return;
                        if (configField.field().get(configField.holder()) != null && Configurable.class.isAssignableFrom(configField.field().get(configField.holder()).getClass()))
                            ((Configurable)configField.field().get(configField.holder())).fromConfig(config.get(configField.path()));
                        else
                            configField.field().set(configField.holder(), configField.field().getType() == String.class ? String.valueOf(config.get(configField.path())) : config.get(configField.path()));
                    } catch (IllegalAccessException e) {
                        log.exception("An exception occurred trying to reload WUtils config", e);
                    }
                });
        log.info("Reloaded WUtils config");
    }

    public void loadConfig(FileConfiguration config, Object object) {
        registeredConfigFields.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(configField -> configField.holder() == object)
                .forEachOrdered(configField -> {
                    configField.field().setAccessible(true);
                    try {
                        if (!config.contains(configField.path()))
                            return;
                        if (configField.field().get(configField.holder()) != null && Configurable.class.isAssignableFrom(configField.field().get(configField.holder()).getClass()))
                            ((Configurable)configField.field().get(configField.holder())).fromConfig(config.get(configField.path()));
                        else
                            configField.field().set(configField.holder(), configField.field().getType() == String.class ? String.valueOf(config.get(configField.path())) : config.get(configField.path()));
                    } catch (IllegalAccessException e) {
                        log.exception("An exception occurred trying to load config field '" + configField.field().getName() + "'", e);
                    }
                });
    }

    public void generateConfig(boolean backup, Map<String, String> replaceVars, List<String> deleteProps)
    {
        if (configGenerator == null)
        {
            log.error("Trying to generate config, but configGenerator is null");
            return;
        }

        configGenerator.copyDefaultConfig();
        configGenerator.writeConfigSections(ConfigFieldParser.getConfigSections(registeredConfigFields));
        configGenerator.generateConfig(backup, replaceVars, deleteProps);
    }

    public void generateConfig()
    {
        generateConfig(true, new HashMap<>(), new ArrayList<>());
    }
}
