package me.wyne.wutils.i18n;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.validation.NullValidator;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class I18n {
    public static I18n global = new I18n();

    private final Map<String, Language> languageMap = new HashMap<>();
    private Language defaultLanguage;
    private Language pluginDefaultLanguage;

    private StringValidator stringValidator = new NullValidator();

    private I18n() {}

    public I18n(File defaultLanguageFile)
    {
        setDefaultLanguage(defaultLanguageFile);
    }

    public I18n(JavaPlugin plugin)
    {
        loadDefaultPluginLanguage(plugin);
        setDefaultLanguage(getDefaultLanguageFile(plugin));
        loadLanguages(plugin);
    }

    public I18n(File defaultLanguageFile, StringValidator stringValidator)
    {
        this.stringValidator = stringValidator;
        setDefaultLanguage(defaultLanguageFile);
    }

    public I18n(JavaPlugin plugin, StringValidator stringValidator)
    {
        this.stringValidator = stringValidator;
        loadDefaultPluginLanguage(plugin);
        setDefaultLanguage(getDefaultLanguageFile(plugin));
        loadLanguages(plugin);
    }

    public void loadLanguage(File languageFile)
    {
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new Language(defaultLanguage, languageFile, stringValidator));
        Log.global.info("Loaded " + FilenameUtils.removeExtension(languageFile.getName()) + " language");
    }

    public void loadLanguage(String languageResourcePath, JavaPlugin plugin)
    {
        File languageResource = new File(plugin.getDataFolder(), "defaults/" + languageResourcePath);
        File languageFile = new File(plugin.getDataFolder(), languageResourcePath);
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(languageResourcePath), languageResource);
        } catch (IOException e) {
            Log.global.exception("An exception occurred while trying to load " + languageResourcePath + " language", e);
        }
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new Language(new Language(languageResource, stringValidator), languageFile, stringValidator));
        Log.global.info("Loaded " + FilenameUtils.removeExtension(languageFile.getName()) + " language");
    }

    private void loadLanguages(JavaPlugin plugin)
    {
        for (File file : new File(plugin.getDataFolder(), "lang").listFiles())
        {
            loadLanguage(file);
        }
    }

    private void loadDefaultPluginLanguage(JavaPlugin plugin)
    {
        File configResource = new File(plugin.getDataFolder(),  "defaults/config.yml");
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource("config.yml"), configResource);
            YamlConfiguration pluginConfig = YamlConfiguration.loadConfiguration(configResource);

            if (!pluginConfig.contains("lang"))
                return;

            File languageResource = new File(plugin.getDataFolder(), "defaults/lang/" + pluginConfig.getString("lang"));
            FileUtils.copyInputStreamToFile(plugin.getResource("lang/" + pluginConfig.getString("lang")), languageResource);
            pluginDefaultLanguage = new Language(languageResource, stringValidator);
        } catch (IOException e) {
            Log.global.exception("An exception occurred while trying to load default plugin language", e);
        }
    }

    public static File getDefaultLanguageFile(JavaPlugin plugin)
    {
        if (!plugin.getConfig().contains("lang", true))
        {
            Log.global.warn("Plugin config doesn't contain default language path");
            Log.global.warn("Absence of default language may and will cause issues");
            return null;
        }

        return new File(plugin.getDataFolder(), "lang/" + plugin.getConfig().getString("lang"));
    }

    public void setDefaultLanguage(File languageFile)
    {
        if (languageFile == null || !languageFile.exists())
        {
            Log.global.error("Couldn't set default language to " + (languageFile != null ? languageFile.getName() : "null"));
            if (defaultLanguage == null)
            {
                Log.global.warn("Will try to get language file from plugin's resources");
                if (pluginDefaultLanguage != null)
                {
                    defaultLanguage = pluginDefaultLanguage;
                    Log.global.warn("Using " + pluginDefaultLanguage.getLanguageCode() + " as default language");
                    return;
                }
                Log.global.warn("Couldn't get language file from plugin's resources");
            }
            return;
        }

        defaultLanguage = pluginDefaultLanguage != null
                ? new Language(pluginDefaultLanguage, languageFile, stringValidator)
                : new Language(languageFile, stringValidator);
        Log.global.info("Default language is set to " + defaultLanguage.getLanguageCode());
    }

    public void setStringValidator(StringValidator stringValidator)
    {
        this.stringValidator = stringValidator;
        languageMap.forEach((s, language) -> language.setStringValidator(stringValidator));
        defaultLanguage.setStringValidator(stringValidator);
    }

    public Language getLanguage(@Nullable Locale locale)
    {
        if (locale == null)
            return defaultLanguage;
        if (locale.getLanguage().isEmpty())
            return defaultLanguage;
        if (!languageMap.containsKey(locale.getLanguage()))
            return defaultLanguage;
        return languageMap.get(locale.getLanguage());
    }

    public String getString(String path)
    {
        return defaultLanguage.getString(path);
    }

    public String getString(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getString(path);
    }

    public String getPlaceholderString(Player player, String path)
    {
        return defaultLanguage.getPlaceholderString(player, path);
    }

    public String getPlaceholderString(@Nullable Locale locale, Player player, String path)
    {
        return getLanguage(locale).getPlaceholderString(player, path);
    }

    public Component getComponent(String path)
    {
        return defaultLanguage.getComponent(path);
    }

    public Component getComponent(String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getComponent(path, tagResolvers);
    }

    public Component getComponent(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getComponent(path);
    }

    public Component getComponent(@Nullable Locale locale, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getComponent(path, tagResolvers);
    }

    public Component getPlaceholderComponent(Player player, String path)
    {
        return defaultLanguage.getPlaceholderComponent(player, path);
    }

    public Component getPlaceholderComponent(Player player, String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getPlaceholderComponent(player, path, tagResolvers);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, Player player, String path)
    {
        return getLanguage(locale).getPlaceholderComponent(player, path);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, Player player, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getPlaceholderComponent(player, path, tagResolvers);
    }

    public List<String> getStringList(String path)
    {
        return defaultLanguage.getStringList(path);
    }

    public List<String> getStringList(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getStringList(path);
    }

    public List<String> getPlaceholderStringList(Player player, String path)
    {
        return defaultLanguage.getPlaceholderStringList(player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, Player player, String path)
    {
        return getLanguage(locale).getPlaceholderStringList(player, path);
    }

    public List<Component> getComponentList(String path)
    {
        return defaultLanguage.getComponentList(path);
    }

    public List<Component> getComponentList(String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getComponentList(path, tagResolvers);
    }

    public List<Component> getComponentList(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getComponentList(path);
    }

    public List<Component> getComponentList(@Nullable Locale locale, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getComponentList(path, tagResolvers);
    }

    public List<Component> getPlaceholderComponentList(Player player, String path)
    {
        return defaultLanguage.getPlaceholderComponentList(player, path);
    }

    public List<Component> getPlaceholderComponentList(Player player, String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getPlaceholderComponentList(player, path, tagResolvers);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, Player player, String path)
    {
        return getLanguage(locale).getPlaceholderComponentList(player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, Player player, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getPlaceholderComponentList(player, path, tagResolvers);
    }

}
