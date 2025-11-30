package me.wyne.wutils.i18n;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class PluginI18nBuilder extends BaseI18nBuilder<PluginI18nBuilder> {

    private final Plugin plugin;

    public PluginI18nBuilder(Plugin plugin) {
        this.plugin = plugin;
    }

    public PluginI18nBuilder loadLanguage(String languageResourcePath) {
        return loadLanguage(plugin, languageResourcePath);
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
            getLogger().error("An exception occurred trying to write resource {} to a file", resourcePath, e);
        }
    }

}
