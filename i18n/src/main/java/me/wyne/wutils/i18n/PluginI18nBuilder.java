package me.wyne.wutils.i18n;

import me.wyne.wutils.i18n.language.BaseLanguage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PluginI18nBuilder extends BaseI18nBuilder {

    private final JavaPlugin plugin;

    public PluginI18nBuilder(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadLanguage(String languageResourcePath) {
        File languageFile = new File(plugin.getDataFolder(), languageResourcePath);
        if (!languageFile.exists())
            plugin.saveResource(languageResourcePath, false);
        File languageResourceFile = new File(plugin.getDataFolder(), "defaults/" + languageResourcePath);
        writeResource(languageResourcePath, languageResourceFile);
        loadLanguage(new BaseLanguage(languageResourceFile, getLog()), languageFile);
    }

    private void loadDefaultLanguage() {
        if (!plugin.getConfig().contains("lang"))
            loadDefaultResourceLanguage();
        else {
            String defaultLanguageCode = FilenameUtils.removeExtension(plugin.getConfig().getString("lang"));
            if (!getLanguageMap().containsKey(defaultLanguageCode))
                loadDefaultResourceLanguage();
            else
                setDefaultLanguageCode(defaultLanguageCode);
        }
    }

    private void loadDefaultResourceLanguage() {
        String configResourcePath = "config.yml";
        File configResourceFile = new File(plugin.getDataFolder(),  "defaults/" + configResourcePath);
        writeResource(configResourcePath, configResourceFile);
        YamlConfiguration resourceConfig = YamlConfiguration.loadConfiguration(configResourceFile);
        if (!resourceConfig.contains("lang"))
            return;
        String defaultLanguagePath = resourceConfig.getString("lang");
        String defaultLanguageCode = FilenameUtils.removeExtension(defaultLanguagePath);
        if (!getLanguageMap().containsKey(defaultLanguageCode)) {
            File languageResourceFile = new File(plugin.getDataFolder(), "defaults/lang/" + defaultLanguagePath);
            writeResource("lang/" + defaultLanguagePath, languageResourceFile);
            loadLanguage(languageResourceFile);
        }
        setDefaultLanguageCode(defaultLanguageCode);
    }

    private void loadLanguages() {
        File directory = new File(plugin.getDataFolder(), "lang");
        loadLanguages(directory);
    }

    @Override
    public I18n build() {
        loadDefaultLanguage();
        loadLanguages();
        return super.build();
    }

    @SuppressWarnings("DataFlowIssue")
    private void writeResource(String resourcePath, File file) {
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(resourcePath), file);
        } catch (IOException e) {
            getLog().error("An exception occurred trying to write resource {} to a file", resourcePath, e);
        }
    }

}
