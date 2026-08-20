package me.wyne.wutils.i18n;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * {@link BaseI18nBuilder} specialization that resolves languages and the default language from a
 * {@link Plugin}'s data folder and {@code config.yml}, rather than requiring the caller to supply them.
 */
public class PluginI18nBuilder extends BaseI18nBuilder<PluginI18nBuilder> {

    private final Plugin plugin;

    public PluginI18nBuilder(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull PluginI18nBuilder loadLanguage(@NotNull String languageResourcePath) {
        return loadLanguage(plugin, languageResourcePath);
    }

    private void loadDefaultLanguage() {
        // Prefers the code configured under "lang" in the plugin's live config, falling back to the
        // bundled default config/language when it is absent or names a language that was not loaded.
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

    /**
     * Loads every language file from {@code <dataFolder>/lang}, then resolves the default language from
     * the plugin's config before delegating to {@link BaseI18nBuilder#build()}. This load-then-resolve
     * order is required: resolving the default first would not see languages that only exist on disk.
     */
    @Override
    public @NotNull I18n build() {
        loadLanguages();
        loadDefaultLanguage();
        return super.build();
    }

    @SuppressWarnings("DataFlowIssue")
    private void writeResource(@NotNull String resourcePath, @NotNull File file) {
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(resourcePath), file);
        } catch (IOException e) {
            getLogger().error("An exception occurred trying to write resource {} to a file", resourcePath, e);
        }
    }

}
