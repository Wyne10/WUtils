package me.wyne.wutils.config;

import me.wyne.wutils.config.configurable.Configurable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Config {

    public static final Config global = new Config();
    public Logger log = LoggerFactory.getLogger(getClass());

    private ConfigGenerator configGenerator;
    /**
     * Key - Section
     */
    private final Map<String, Set<ConfigField>> registeredConfigFields = new LinkedHashMap<>();

    static {
        try {
            Configurator.setLevel("ru.vyarus", Level.WARN);
            Configurator.setLevel(LogManager.getLogger("ru.vyarus"), Level.WARN);
        } catch (NoSuchMethodError ignored) {}
    }

    public void setConfigGenerator(File configFile, File defaultConfigFile) {
        configGenerator = new ConfigGenerator(configFile, defaultConfigFile, log);
    }

    public void setConfigGenerator(JavaPlugin plugin, String configPath) {
        Path defaultConfigPath = Path.of(plugin.getDataFolder().getAbsolutePath(), "defaults/", configPath);
        File defaultConfig = new File(plugin.getDataFolder(), "defaults/" + configPath);
        try {
            Files.createDirectories(defaultConfigPath.getParent());
            if (!defaultConfig.exists())
                defaultConfig.createNewFile();
            Files.copy(plugin.getResource(configPath), defaultConfigPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("An exception occurred trying to load default config for WUtils config", e);
        }
        setConfigGenerator(new File(plugin.getDataFolder(), configPath), defaultConfig);
    }

    public void registerConfigObject(Object object) {
        for(Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigEntry.class))
                continue;
            Pair<String, ConfigField> sectionedConfigField = ConfigFieldParser.getSectionedConfigField(object, field, log);
            registerConfigField(sectionedConfigField.getValue0(), sectionedConfigField.getValue1());
        }
    }

    public void registerConfigField(String section, ConfigField field) {
        if (!registeredConfigFields.containsKey(section) || registeredConfigFields.get(section) == null)
            registeredConfigFields.put(section, new LinkedHashSet<>());

        registeredConfigFields.get(section).add(field);
    }

    public void reloadConfig(ConfigurationSection config) {
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
                        log.error("An exception occurred trying to reload WUtils config", e);
                    }
                });
        log.info("Reloaded WUtils config");
    }

    public void loadConfig(ConfigurationSection config, Object object) {
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
                        log.error("An exception occurred trying to load config field '{}'", configField.field().getName(), e);
                    }
                });
    }

    public void generateConfig(boolean backup, Map<String, String> replaceVars, List<String> deleteProps) {
        if (configGenerator == null) {
            log.error("Trying to generate config, but configGenerator is null");
            return;
        }

        deleteProps.add("regenerate");
        configGenerator.copyDefaultConfig();
        configGenerator.writeConfigSections(ConfigFieldParser.getConfigSections(registeredConfigFields));
        configGenerator.generateConfig(backup, replaceVars, deleteProps);
    }

    public void generateConfig() {
        generateConfig(true, new HashMap<>(), new ArrayList<>());
    }

}
