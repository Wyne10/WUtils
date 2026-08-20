package me.wyne.wutils.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import ru.vyarus.yaml.updater.YamlUpdater;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Assembles a plugin's default config file out of registered {@link ConfigSection}s and, when the live
 * config requests it, merges that regenerated default into the live config on disk via
 * {@code yaml-config-updater}.
 */
public class ConfigGenerator {

    private final Logger logger;

    private final File configFile;
    private final File defaultConfigFile;

    private final StringBuilder generatedText = new StringBuilder();

    public ConfigGenerator(@NotNull File configFile, @NotNull File defaultConfigFile, @NotNull Logger logger) {
        this.logger = logger;
        this.configFile = configFile;
        this.defaultConfigFile = defaultConfigFile;
    }

    public void writeConfigSection(@NotNull ConfigSection section)
    {
        generatedText.append(section.generateConfigSection());
    }

    public void writeConfigSections(@NotNull Set<@NotNull ConfigSection> sectionSet) {
        for (ConfigSection section : sectionSet) {
            writeConfigSection(section);
        }
    }

    /**
     * Reads the existing default config file into the in-progress generated text, skipping its first
     * line by design — callers replacing or hand-editing the default config file must account for that
     * first line being discarded on the next generation.
     */
    public void copyDefaultConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(defaultConfigFile))) {
            reader.lines().skip(1).forEachOrdered(s -> {
                generatedText.append(s);
                generatedText.append("\n");
            });
        } catch (IOException e) {
            logger.error("An exception occurred trying to read default config file", e);
        }
    }

    /**
     * Writes the generated text to the default config file unconditionally, then merges it into the
     * live config file, but only if the live config currently contains {@code regenerate: true}.
     *
     * @param replaceVars variables substituted by {@code yaml-config-updater} while merging
     * @param deleteProps property paths removed from the live config while merging; only consulted, not
     *                    mutated, by this method
     */
    public void generateConfig(boolean backup, @NotNull Map<@NotNull String, @NotNull String> replaceVars, @NotNull List<@NotNull String> deleteProps) {
        boolean generationRequested = YamlConfiguration.loadConfiguration(configFile).getBoolean("regenerate", false);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(defaultConfigFile))) {
            writer.write(generatedText.toString());
            writer.flush();
            logger.debug("Generated default WUtils config");
        } catch (IOException e) {
            logger.error("An exception occurred trying to write WUtils config", e);
        }

        if (!generationRequested)
            return;
        YamlUpdater.create(configFile, defaultConfigFile)
                .backup(backup)
                .backupDir(new File(configFile.getParentFile(), "backups"))
                .vars(replaceVars)
                .deleteProps(deleteProps)
                .update();
        logger.debug("Merged WUtils config");
    }

}
