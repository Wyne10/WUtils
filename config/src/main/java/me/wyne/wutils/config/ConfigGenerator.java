package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.util.Set;

public class ConfigGenerator {

    private final File configFile;
    private final FileConfiguration config;
    private final StringBuilder generatedText = new StringBuilder();

    private boolean isNewVersion = false;

    public ConfigGenerator(File configFile, FileConfiguration config)
    {
        this.configFile = configFile;
        this.config = config;
    }

    private void copyConfigFileData()
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            reader.lines().forEachOrdered(s -> generatedText.append(s));
            generatedText.append("\n");
        } catch (IOException e) {
            Log.global.exception("An exception occurred while trying to read config file data", e);
        }
    }

    public void writeVersion(String version)
    {
        if (config.contains("config-version") && config.getString("config-version").equals(version))
            return;
        isNewVersion = true;
        generatedText.insert(0, "config-version: " + version + "\n");
        copyConfigFileData();
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

    public void generateConfig()
    {
        if (!isNewVersion)
            return;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(generatedText.toString());
            writer.flush();
            Log.global.info("Generated WUtils config");
        } catch (IOException e) {
            Log.global.exception("An exception occurred while trying to write WUtils config", e);
        }
    }

}
