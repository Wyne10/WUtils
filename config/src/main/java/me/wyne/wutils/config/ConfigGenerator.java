package me.wyne.wutils.config;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.vyarus.yaml.updater.YamlUpdater;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigGenerator {

    private final LogWrapper log;

    private final File configFile;
    private final File defaultConfigFile;

    private final StringBuilder generatedText = new StringBuilder();

    public ConfigGenerator(File configFile, File defaultConfigFile, LogWrapper log)
    {
        this.log = log;
        this.configFile = configFile;
        this.defaultConfigFile = defaultConfigFile;
    }

    public void writeConfigSection(ConfigSection section)
    {
        generatedText.append(section.generateConfigSection());
    }

    public void writeConfigSections(Set<ConfigSection> sectionSet)
    {
        for (ConfigSection section : sectionSet)
        {
            writeConfigSection(section);
        }
    }

    public void copyDefaultConfig()
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(defaultConfigFile))) {
            reader.lines().forEachOrdered(s -> {
                if (s.contains("regenerate"))
                    return;

                generatedText.append(s);
                generatedText.append("\n");
            });
            generatedText.append("\n");
        } catch (IOException e) {
            log.exception("An exception occurred trying to read default config file data", e);
        }
    }

    public void generateConfig(boolean backup, Map<String, String> replaceVars, List<String> deleteProps)
    {
        boolean generationRequested = YamlConfiguration.loadConfiguration(configFile).getBoolean("regenerate", false);
        if (!generationRequested)
            return;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(defaultConfigFile))) {
            writer.write(generatedText.toString());
            writer.flush();
            YamlUpdater.create(configFile, defaultConfigFile)
                    .backup(backup)
                    .backupDir(new File(configFile.getParentFile(), "backups"))
                    .vars(replaceVars)
                    .deleteProps(deleteProps)
                    .update();
            log.info("Generated WUtils config");
        } catch (IOException e) {
            log.exception("An exception occurred trying to write WUtils config", e);
        }
    }

}
