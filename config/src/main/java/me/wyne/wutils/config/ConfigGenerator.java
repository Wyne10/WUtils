package me.wyne.wutils.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import ru.vyarus.yaml.updater.YamlUpdater;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigGenerator {

    private final Logger log;

    private final File configFile;
    private final File defaultConfigFile;

    private final StringBuilder generatedText = new StringBuilder();

    public ConfigGenerator(File configFile, File defaultConfigFile, Logger log) {
        this.log = log;
        this.configFile = configFile;
        this.defaultConfigFile = defaultConfigFile;
    }

    public void writeConfigSection(ConfigSection section)
    {
        generatedText.append(section.generateConfigSection());
    }

    public void writeConfigSections(Set<ConfigSection> sectionSet) {
        for (ConfigSection section : sectionSet) {
            writeConfigSection(section);
        }
    }

    public void copyDefaultConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(defaultConfigFile))) {
            reader.lines().skip(1).forEachOrdered(s -> {
                generatedText.append(s);
                generatedText.append("\n");
            });
        } catch (IOException e) {
            log.error("An exception occurred trying to read default config file", e);
        }
    }

    public void generateConfig(boolean backup, Map<String, String> replaceVars, List<String> deleteProps) {
        boolean generationRequested = YamlConfiguration.loadConfiguration(configFile).getBoolean("regenerate", false);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(defaultConfigFile))) {
            writer.write(generatedText.toString());
            writer.flush();
        } catch (IOException e) {
            log.error("An exception occurred trying to write WUtils config", e);
        }

        if (!generationRequested)
            return;
        YamlUpdater.create(configFile, defaultConfigFile)
                .backup(backup)
                .backupDir(new File(configFile.getParentFile(), "backups"))
                .vars(replaceVars)
                .deleteProps(deleteProps)
                .update();
    }

}
