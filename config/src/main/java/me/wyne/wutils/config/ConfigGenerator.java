package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.vyarus.yaml.updater.YamlUpdater;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigGenerator {

    private final File configFile;
    private final File defaultConfigFile;

    private final FileConfiguration existingConfig;

    private final StringBuilder generatedText = new StringBuilder();

    public ConfigGenerator(File configFile, File defaultConfigFile)
    {
        this.configFile = configFile;
        this.defaultConfigFile = defaultConfigFile;
        this.existingConfig = YamlConfiguration.loadConfiguration(configFile);
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
            reader.lines().forEachOrdered(s -> { generatedText.append(s); generatedText.append("\n"); });
            generatedText.append("\n");
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to read default config file data", e);
        }
    }

    public void generateConfig(boolean backup, Map<String, String> replaceVars, List<String> deleteProps)
    {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(defaultConfigFile))) {
            writer.write(generatedText.toString());
            writer.flush();
            YamlUpdater.create(configFile, defaultConfigFile)
                    .backup(backup)
                    .backupDir(new File(configFile.getParentFile(), "backups"))
                    .vars(replaceVars)
                    .deleteProps(deleteProps)
                    .update();
            Log.global.info("Generated WUtils config");
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to write WUtils config", e);
        }
    }

}
