package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Set;

public class ConfigGenerator {

    private final File configFile;
    private final File defaultConfigFile;
    private final FileConfiguration existingConfig;
    private final StringBuilder generatedText = new StringBuilder();

    private boolean isNewVersion = false;

    public ConfigGenerator(File configFile, File defaultConfigFile, FileConfiguration existingConfig)
    {
        this.configFile = configFile;
        this.defaultConfigFile = defaultConfigFile;
        this.existingConfig = existingConfig;
    }

    public void writeVersion(String version)
    {
        if (existingConfig.contains("config-version") && existingConfig.getString("config-version").equals(version))
            return;
        isNewVersion = true;
        generatedText.insert(0, "config-version: " + version + "\n");
        copyDefaultConfig();
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

    private void copyDefaultConfig()
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(defaultConfigFile))) {
            reader.lines().forEachOrdered(s -> { generatedText.append(s); generatedText.append("\n"); });
            generatedText.append("\n");
        } catch (IOException e) {
            Log.global.exception("An exception occurred while trying to read default config file data", e);
        }
    }

    private void mergeExistingConfig() throws IOException {
        FileConfiguration newConfig = YamlConfiguration.loadConfiguration(configFile);
        for (String key : newConfig.getKeys(false))
        {
            if (key.equals("config-version"))
                continue;
            if (existingConfig.contains(key))
                newConfig.set(key, existingConfig.get(key));
        }
        newConfig.save(configFile);
    }

    public void generateConfig()
    {
        if (!isNewVersion)
            return;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(generatedText.toString());
            writer.flush();
            mergeExistingConfig();
            Log.global.info("Generated WUtils config");
        } catch (IOException e) {
            Log.global.exception("An exception occurred while trying to write WUtils config", e);
        }
    }

}
