package me.wyne.wutils.config;

import me.wyne.wutils.config.configurable.ConfigDeserializable;
import me.wyne.wutils.config.configurable.ConfigSerializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Registers {@link ConfigEntry}-annotated fields and drives config generation and reloading for them.
 *
 * <p>A shared {@link #global} instance is provided for convenience, but nothing forces its use — a
 * consumer may construct and hold its own {@code Config} instance instead.</p>
 *
 * <p>The static initializer lowers the {@code ru.vyarus} logger (used by the bundled
 * {@code yaml-config-updater}) to {@link Level#WARN} to silence its default logging, guarded against
 * {@link NoSuchMethodError} on log4j runtimes that don't support the API used to do so.</p>
 */
public class Config {

    public static final @NotNull Config global = new Config();
    public @NotNull Logger logger = PluginUtils.getLogger(getClass());

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

    /**
     * Sets the generator used by {@link #generateConfig} directly from explicit file locations.
     */
    public void setConfigGenerator(@NotNull File configFile, @NotNull File defaultConfigFile) {
        configGenerator = new ConfigGenerator(configFile, defaultConfigFile, logger);
    }

    /**
     * Sets the generator used by {@link #generateConfig}, copying the plugin's bundled resource at
     * {@code configPath} out to {@code <dataFolder>/defaults/<configPath>} as the starting default
     * config file.
     *
     * <p>If {@code configPath} does not name a resource bundled in the plugin's jar,
     * {@link Plugin#getResource} returns {@code null}, which is passed straight into
     * {@link Files#copy(java.io.InputStream, Path, java.nio.file.CopyOption...)}. The resulting
     * {@link NullPointerException} is <em>not</em> caught by this method's {@code IOException}
     * handling and propagates to the caller uncaught. The config generator is still assigned
     * afterward even if the copy failed with a (caught) {@code IOException}.</p>
     */
    public void setConfigGenerator(@NotNull Plugin plugin, @NotNull String configPath) {
        Path defaultConfigPath = Path.of(plugin.getDataFolder().getAbsolutePath(), "defaults/", configPath);
        File defaultConfig = new File(plugin.getDataFolder(), "defaults/" + configPath);
        try {
            Files.createDirectories(defaultConfigPath.getParent());
            if (!defaultConfig.exists())
                defaultConfig.createNewFile();
            Files.copy(plugin.getResource(configPath), defaultConfigPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("An exception occurred trying to load default config for WUtils config", e);
        }
        setConfigGenerator(new File(plugin.getDataFolder(), configPath), defaultConfig);
    }

    /**
     * Collects every {@link ConfigEntry}-annotated field declared directly on {@code object}'s class
     * and registers each under its annotation's section.
     *
     * <p>Uses {@link Class#getDeclaredFields()} — fields inherited from a superclass are not picked up,
     * only ones declared on {@code object.getClass()} itself.</p>
     */
    public void registerConfigObject(@NotNull Object object) {
        for(Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigEntry.class))
                continue;
            Pair<String, ConfigField> sectionedConfigField = ConfigFieldParser.getSectionedConfigField(object, field, logger);
            registerConfigField(sectionedConfigField.getValue0(), sectionedConfigField.getValue1());
        }
    }

    public void registerConfigField(@NotNull String section, @NotNull ConfigField field) {
        if (!registeredConfigFields.containsKey(section) || registeredConfigFields.get(section) == null)
            registeredConfigFields.put(section, new LinkedHashSet<>());

        registeredConfigFields.get(section).add(field);
    }

    /**
     * Reloads every registered field (across every registered holder object) whose
     * {@link ConfigEntry#load()} is {@code true} and whose path exists in {@code config}.
     *
     * <p>A field whose current value is a {@link ConfigDeserializable} is repopulated in place via
     * {@link ConfigDeserializable#fromConfig}. A field whose current value is a plain
     * {@link ConfigSerializable} (serialize-only) is skipped. Any other field is set directly from
     * {@code config}, coercing the read value to a string via {@link String#valueOf} when the field's
     * declared type is {@link String} — so a YAML number or boolean loads as its string form rather
     * than failing.</p>
     */
    public void reloadConfig(@NotNull ConfigurationSection config) {
        registeredConfigFields.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(ConfigField::load)
                .forEachOrdered(configField -> {
                    configField.field().setAccessible(true);
                    try {
                        if (!config.contains(configField.path()))
                            return;
                        Object fieldValue = configField.field().get(configField.holder());
                        if (fieldValue instanceof ConfigDeserializable)
                            ((ConfigDeserializable) fieldValue).fromConfig(config.get(configField.path()));
                        else if (fieldValue instanceof ConfigSerializable)
                            return;
                        else
                            configField.field().set(configField.holder(), configField.field().getType() == String.class ? String.valueOf(config.get(configField.path())) : config.get(configField.path()));
                        logger.debug("Reloaded WUtils config");
                    } catch (IllegalAccessException e) {
                        logger.error("An exception occurred trying to load config field '{}'", configField.field().getName(), e);
                    }
                });
    }

    /**
     * Reloads registered fields the same way {@link #reloadConfig} does, but restricted to fields whose
     * registered holder is the exact same instance ({@code ==}, not {@link Object#equals}) as
     * {@code object} — a field registered on a different, even equal, instance is left untouched.
     */
    public void loadConfig(@NotNull ConfigurationSection config, @NotNull Object object) {
        registeredConfigFields.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(ConfigField::load)
                .filter(configField -> configField.holder() == object)
                .forEachOrdered(configField -> {
                    configField.field().setAccessible(true);
                    try {
                        if (!config.contains(configField.path()))
                            return;
                        Object fieldValue = configField.field().get(configField.holder());
                        if (fieldValue instanceof ConfigDeserializable)
                            ((ConfigDeserializable) fieldValue).fromConfig(config.get(configField.path()));
                        else if (fieldValue instanceof ConfigSerializable)
                            return;
                        else
                            configField.field().set(configField.holder(), configField.field().getType() == String.class ? String.valueOf(config.get(configField.path())) : config.get(configField.path()));
                        logger.debug("Reloaded WUtils config for object '{}'", object.getClass().getSimpleName());
                    } catch (IllegalAccessException e) {
                        logger.error("An exception occurred trying to load config field '{}'", configField.field().getName(), e);
                    }
                });
    }

    /**
     * Regenerates the default config file from every registered field and, if the live config requests
     * it, merges that default into the live config. A no-op (logged as an error) if
     * {@link #setConfigGenerator} has not been called yet.
     *
     * <p>Mutates {@code deleteProps} in place by adding {@code "regenerate"} to it, so that property is
     * always deleted from the live config on merge — passing an immutable list throws
     * {@link UnsupportedOperationException}.</p>
     */
    public void generateConfig(boolean backup, @NotNull Map<@NotNull String, @NotNull String> replaceVars, @NotNull List<@NotNull String> deleteProps) {
        if (configGenerator == null) {
            logger.error("Trying to generate config, but configGenerator is null");
            return;
        }

        deleteProps.add("regenerate");
        configGenerator.copyDefaultConfig();
        configGenerator.writeConfigSections(ConfigFieldParser.getConfigSections(registeredConfigFields));
        configGenerator.generateConfig(backup, replaceVars, deleteProps);
    }

    /**
     * Regenerates the default config with backups enabled and no variable replacements or extra
     * deleted properties. See {@link #generateConfig(boolean, Map, List)}.
     */
    public void generateConfig() {
        generateConfig(true, new HashMap<>(), new ArrayList<>());
    }

}
