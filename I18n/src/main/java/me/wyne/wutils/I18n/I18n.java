package me.wyne.wutils.i18n;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.NullValidator;
import me.wyne.wutils.i18n.language.validation.StringValidator;
import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class I18n {
    public static I18n global = new I18n();

    private final Map<String, Language> languageMap = new HashMap<>();
    private Language defaultLanguage;
    private Language defaultResourceLanguage;

    private StringValidator stringValidator = new NullValidator();

    public I18n() {}

    public I18n(File defaultLanguageFile)
    {
        setDefaultLanguage(defaultLanguageFile);
    }

    public I18n(JavaPlugin plugin)
    {
        loadDefaultResourceLanguage(plugin);
        loadLanguages(plugin);
        setDefaultLanguage(getDefaultLanguageCode(plugin));
    }

    public I18n(File defaultLanguageFile, StringValidator stringValidator)
    {
        this.stringValidator = stringValidator;
        setDefaultLanguage(defaultLanguageFile);
    }

    public I18n(JavaPlugin plugin, StringValidator stringValidator)
    {
        this.stringValidator = stringValidator;
        loadDefaultResourceLanguage(plugin);
        loadLanguages(plugin);
        setDefaultLanguage(getDefaultLanguageCode(plugin));
    }

    public void loadLanguage(File languageFile)
    {
        if (languageMap.containsKey(FilenameUtils.removeExtension(languageFile.getName())))
            return;
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new Language(defaultResourceLanguage, languageFile, stringValidator));
        Log.global.info("Loaded " + FilenameUtils.removeExtension(languageFile.getName()) + " language");
    }

    public void loadLanguage(String languageResourcePath, InputStream languageResourceStream, File dataFolder)
    {
        File languageResource = new File(dataFolder, "defaults/" + languageResourcePath);
        File languageFile = new File(dataFolder, languageResourcePath);
        try {
            FileUtils.copyInputStreamToFile(languageResourceStream, languageResource);
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to load " + languageResourcePath + " language", e); 
        }
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new Language(new Language(languageResource, stringValidator), languageFile, stringValidator));
        Log.global.info("Loaded " + FilenameUtils.removeExtension(languageFile.getName()) + " language");
    }

    public void loadLanguage(String languageResourcePath, JavaPlugin plugin)
    {
        plugin.saveResource(languageResourcePath, false);
        File languageResource = new File(plugin.getDataFolder(), "defaults/" + languageResourcePath);
        File languageFile = new File(plugin.getDataFolder(), languageResourcePath);
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(languageResourcePath), languageResource);
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to load " + languageResourcePath + " language", e);
        }
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new Language(new Language(languageResource, stringValidator), languageFile, stringValidator));
        Log.global.info("Loaded " + FilenameUtils.removeExtension(languageFile.getName()) + " language");
    }

    public void loadLanguages(JavaPlugin plugin)
    {
        for (File file : new File(plugin.getDataFolder(), "lang").listFiles())
        {
            loadLanguage(file);
        }
    }

    public void loadLanguages(File langFolder)
    {
        for (File file : langFolder.listFiles())
        {
            loadLanguage(file);
        }
    }

    public void loadDefaultResourceLanguage(JavaPlugin plugin)
    {
        File configResource = new File(plugin.getDataFolder(),  "defaults/config.yml");
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource("config.yml"), configResource);
            YamlConfiguration pluginConfig = YamlConfiguration.loadConfiguration(configResource);

            if (!pluginConfig.contains("lang"))
                return;

            File languageResource = new File(plugin.getDataFolder(), "defaults/lang/" + pluginConfig.getString("lang"));
            FileUtils.copyInputStreamToFile(plugin.getResource("lang/" + pluginConfig.getString("lang")), languageResource);
            defaultResourceLanguage = new Language(languageResource, stringValidator);
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to load default language from resources", e);
        }
    }

    public void loadDefaultResourceLanguage(File dataFolder, JavaPlugin plugin, Function<String, InputStream> resourceProvider)
    {
        File configResource = new File(dataFolder,  "defaults/config.yml");
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource("config.yml"), configResource);
            YamlConfiguration pluginConfig = YamlConfiguration.loadConfiguration(configResource);

            if (!pluginConfig.contains("lang"))
                return;

            File languageResource = new File(dataFolder, "defaults/lang/" + pluginConfig.getString("lang"));
            FileUtils.copyInputStreamToFile(resourceProvider.apply("lang/" + pluginConfig.getString("lang")), languageResource);
            defaultResourceLanguage = new Language(languageResource, stringValidator);
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to load default plugin language", e);
        }
    }

    @Nullable
    public static String getDefaultLanguageCode(JavaPlugin plugin)
    {
        if (!plugin.getConfig().contains("lang", true))
        {
            Log.global.warn("Plugin config doesn't contain default language path");
            Log.global.warn("Absence of default language may and will cause issues");
            return null;
        }

        return FilenameUtils.removeExtension(plugin.getConfig().getString("lang"));
    }

    public void setDefaultLanguage(@Nullable File languageFile)
    {
        if (languageFile == null || !languageFile.exists())
        {
            Log.global.error("Couldn't set default language to " + (languageFile != null ? languageFile.getName() : "null"));
            if (defaultLanguage == null)
            {
                Log.global.warn("Will try to get language file from plugins resources");
                if (defaultResourceLanguage != null)
                {
                    defaultLanguage = defaultResourceLanguage;
                    Log.global.warn("Using " + defaultResourceLanguage.getLanguageCode() + " as default language");
                    return;
                }
                Log.global.warn("Couldn't get language file from plugin's resources");
            }
            return;
        }

        defaultLanguage = defaultResourceLanguage != null
                ? new Language(defaultResourceLanguage, languageFile, stringValidator)
                : new Language(languageFile, stringValidator);
        Log.global.info("Default language is set to " + defaultLanguage.getLanguageCode());
    }

    public void setDefaultLanguage(String languageCode)
    {
        if (!languageMap.containsKey(languageCode))
        {
            Log.global.error("Couldn't set default language to " + languageCode);
            if (defaultLanguage == null)
            {
                Log.global.warn("Will try to get language file from plugins resources");
                if (defaultResourceLanguage != null)
                {
                    defaultLanguage = defaultResourceLanguage;
                    Log.global.warn("Using " + defaultResourceLanguage.getLanguageCode() + " as default language");
                    return;
                }
                Log.global.warn("Couldn't get language file from plugin's resources");
            }
            return;
        }

        defaultLanguage = languageMap.get(languageCode);
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

    public boolean contains(String path)
    {
        return defaultLanguage.contains(path);
    }

    public boolean contains(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).contains(path);
    }

    public String getString(String path)
    {
        return defaultLanguage.getString(path);
    }

    public String getString(String path, TextReplacement... textReplacements)
    {
        return defaultLanguage.getString(path, textReplacements);
    }

    public String getString(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getString(path);
    }

    public String getString(@Nullable Locale locale, String path, TextReplacement ... textReplacements)
    {
        return getLanguage(locale).getString(path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Player player, String path)
    {
        return defaultLanguage.getPlaceholderString(player, path);
    }

    public String getPlaceholderString(@Nullable OfflinePlayer player, String path)
    {
        return defaultLanguage.getPlaceholderString(player, path);
    }

    public String getPlaceholderString(@Nullable CommandSender sender, String path)
    {
        return defaultLanguage.getPlaceholderString(toPlayer(sender), path);
    }

    public String getPlaceholderString(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderString(player, path, textReplacements);
    }

    public String getPlaceholderString(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderString(player, path, textReplacements);
    }

    public String getPlaceholderString(@Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderString(toPlayer(sender), path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path)
    {
        return getLanguage(locale).getPlaceholderString(player, path);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path)
    {
        return getLanguage(locale).getPlaceholderString(player, path);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable CommandSender sender, String path)
    {
        return getLanguage(locale).getPlaceholderString(toPlayer(sender), path);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderString(player, path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderString(player, path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderString(toPlayer(sender), path, textReplacements);
    }

    public String getLegacyString(String path)
    {
        return defaultLanguage.getLegacyString(path);
    }

    public String getLegacyString(String path, TextReplacement... textReplacements)
    {
        return defaultLanguage.getLegacyString(path, textReplacements);
    }

    public String getLegacyString(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getLegacyString(path);
    }

    public String getLegacyString(@Nullable Locale locale, String path, TextReplacement ... textReplacements)
    {
        return getLanguage(locale).getLegacyString(path, textReplacements);
    }

    public String getLegacyPlaceholderString(@Nullable Player player, String path)
    {
        return defaultLanguage.getLegacyPlaceholderString(player, path);
    }

    public String getLegacyPlaceholderString(@Nullable OfflinePlayer player, String path)
    {
        return defaultLanguage.getLegacyPlaceholderString(player, path);
    }

    public String getLegacyPlaceholderString(@Nullable CommandSender sender, String path)
    {
        return defaultLanguage.getLegacyPlaceholderString(toPlayer(sender), path);
    }

    public String getLegacyPlaceholderString(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderString(player, path, textReplacements);
    }

    public String getLegacyPlaceholderString(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderString(player, path, textReplacements);
    }

    public String getLegacyPlaceholderString(@Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderString(toPlayer(sender), path, textReplacements);
    }

    public String getLegacyPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderString(player, path);
    }

    public String getLegacyPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderString(player, path);
    }

    public String getLegacyPlaceholderString(@Nullable Locale locale, @Nullable CommandSender sender, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderString(toPlayer(sender), path);
    }

    public String getLegacyPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderString(player, path, textReplacements);
    }

    public String getLegacyPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderString(player, path, textReplacements);
    }

    public String getLegacyPlaceholderString(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderString(toPlayer(sender), path, textReplacements);
    }

    public Component getComponent(String path)
    {
        return defaultLanguage.getComponent(path);
    }

    public Component getComponent(String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getComponent(path, tagResolvers);
    }

    public Component getComponent(String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getComponent(path, textReplacements);
    }

    public Component getComponent(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getComponent(path);
    }

    public Component getComponent(@Nullable Locale locale, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getComponent(path, tagResolvers);
    }

    public Component getComponent(@Nullable Locale locale, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getComponent(path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Player player, String path)
    {
        return defaultLanguage.getPlaceholderComponent(player, path);
    }

    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path)
    {
        return defaultLanguage.getPlaceholderComponent(player, path);
    }

    public Component getPlaceholderComponent(@Nullable CommandSender sender, String path)
    {
        return defaultLanguage.getPlaceholderComponent(toPlayer(sender), path);
    }

    public Component getPlaceholderComponent(@Nullable Player player, String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getPlaceholderComponent(player, path, tagResolvers);
    }

    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getPlaceholderComponent(player, path, tagResolvers);
    }

    public Component getPlaceholderComponent(@Nullable CommandSender sender, String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getPlaceholderComponent(toPlayer(sender), path, tagResolvers);
    }

    public Component getPlaceholderComponent(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderComponent(player, path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderComponent(player, path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderComponent(toPlayer(sender), path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path)
    {
        return getLanguage(locale).getPlaceholderComponent(player, path);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path)
    {
        return getLanguage(locale).getPlaceholderComponent(player, path);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable CommandSender sender, String path)
    {
        return getLanguage(locale).getPlaceholderComponent(toPlayer(sender), path);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getPlaceholderComponent(player, path, tagResolvers);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getPlaceholderComponent(player, path, tagResolvers);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable CommandSender sender, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getPlaceholderComponent(toPlayer(sender), path, tagResolvers);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderComponent(player, path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderComponent(player, path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderComponent(toPlayer(sender), path, textReplacements);
    }

    public Component getLegacyComponent(String path)
    {
        return defaultLanguage.getLegacyComponent(path);
    }

    public Component getLegacyComponent(String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyComponent(path, textReplacements);
    }

    public Component getLegacyComponent(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getLegacyComponent(path);
    }

    public Component getLegacyComponent(@Nullable Locale locale, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyComponent(path, textReplacements);
    }

    public Component getLegacyPlaceholderComponent(@Nullable Player player, String path)
    {
        return defaultLanguage.getLegacyPlaceholderComponent(player, path);
    }

    public Component getLegacyPlaceholderComponent(@Nullable OfflinePlayer player, String path)
    {
        return defaultLanguage.getLegacyPlaceholderComponent(player, path);
    }

    public Component getLegacyPlaceholderComponent(@Nullable CommandSender sender, String path)
    {
        return defaultLanguage.getLegacyPlaceholderComponent(toPlayer(sender), path);
    }

    public Component getLegacyPlaceholderComponent(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderComponent(player, path, textReplacements);
    }

    public Component getLegacyPlaceholderComponent(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderComponent(player, path, textReplacements);
    }

    public Component getLegacyPlaceholderComponent(@Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderComponent(toPlayer(sender), path, textReplacements);
    }

    public Component getLegacyPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderComponent(player, path);
    }

    public Component getLegacyPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderComponent(player, path);
    }

    public Component getLegacyPlaceholderComponent(@Nullable Locale locale, @Nullable CommandSender sender, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderComponent(toPlayer(sender), path);
    }

    public Component getLegacyPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderComponent(player, path, textReplacements);
    }

    public Component getLegacyPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderComponent(player, path, textReplacements);
    }

    public Component getLegacyPlaceholderComponent(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderComponent(toPlayer(sender), path, textReplacements);
    }

    public List<String> getStringList(String path)
    {
        return defaultLanguage.getStringList(path);
    }

    public List<String> getStringList(String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getStringList(path, textReplacements);
    }

    public List<String> getStringList(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getStringList(path);
    }

    public List<String> getStringList(@Nullable Locale locale, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getStringList(path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Player player, String path)
    {
        return defaultLanguage.getPlaceholderStringList(player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable OfflinePlayer player, String path)
    {
        return defaultLanguage.getPlaceholderStringList(player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable CommandSender sender, String path)
    {
        return defaultLanguage.getPlaceholderStringList(toPlayer(sender), path);
    }

    public List<String> getPlaceholderStringList(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderStringList(player, path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderStringList(player, path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderStringList(toPlayer(sender), path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path)
    {
        return getLanguage(locale).getPlaceholderStringList(player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path)
    {
        return getLanguage(locale).getPlaceholderStringList(player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable CommandSender sender, String path)
    {
        return getLanguage(locale).getPlaceholderStringList(toPlayer(sender), path);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderStringList(player, path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderStringList(player, path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderStringList(toPlayer(sender), path, textReplacements);
    }

    public List<String> getLegacyStringList(String path)
    {
        return defaultLanguage.getLegacyStringList(path);
    }

    public List<String> getLegacyStringList(String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyStringList(path, textReplacements);
    }

    public List<String> getLegacyStringList(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getLegacyStringList(path);
    }

    public List<String> getLegacyStringList(@Nullable Locale locale, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyStringList(path, textReplacements);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Player player, String path)
    {
        return defaultLanguage.getLegacyPlaceholderStringList(player, path);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable OfflinePlayer player, String path)
    {
        return defaultLanguage.getLegacyPlaceholderStringList(player, path);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable CommandSender sender, String path)
    {
        return defaultLanguage.getLegacyPlaceholderStringList(toPlayer(sender), path);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderStringList(player, path, textReplacements);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderStringList(player, path, textReplacements);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderStringList(toPlayer(sender), path, textReplacements);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderStringList(player, path);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderStringList(player, path);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Locale locale, @Nullable CommandSender sender, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderStringList(toPlayer(sender), path);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderStringList(player, path, textReplacements);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderStringList(player, path, textReplacements);
    }

    public List<String> getLegacyPlaceholderStringList(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderStringList(toPlayer(sender), path, textReplacements);
    }

    public List<Component> getComponentList(String path)
    {
        return defaultLanguage.getComponentList(path);
    }

    public List<Component> getComponentList(String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getComponentList(path, tagResolvers);
    }

    public List<Component> getComponentList(String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getComponentList(path, textReplacements);
    }

    public List<Component> getComponentList(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getComponentList(path);
    }

    public List<Component> getComponentList(@Nullable Locale locale, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getComponentList(path, tagResolvers);
    }

    public List<Component> getComponentList(@Nullable Locale locale, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getComponentList(path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path)
    {
        return defaultLanguage.getPlaceholderComponentList(player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path)
    {
        return defaultLanguage.getPlaceholderComponentList(player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable CommandSender sender, String path)
    {
        return defaultLanguage.getPlaceholderComponentList(toPlayer(sender), path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getPlaceholderComponentList(player, path, tagResolvers);
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getPlaceholderComponentList(player, path, tagResolvers);
    }

    public List<Component> getPlaceholderComponentList(@Nullable CommandSender sender, String path, TagResolver ...tagResolvers)
    {
        return defaultLanguage.getPlaceholderComponentList(toPlayer(sender), path, tagResolvers);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderComponentList(player, path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderComponentList(player, path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getPlaceholderComponentList(toPlayer(sender), path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path)
    {
        return getLanguage(locale).getPlaceholderComponentList(player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path)
    {
        return getLanguage(locale).getPlaceholderComponentList(player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable CommandSender sender, String path)
    {
        return getLanguage(locale).getPlaceholderComponentList(toPlayer(sender), path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getPlaceholderComponentList(player, path, tagResolvers);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getPlaceholderComponentList(player, path, tagResolvers);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable CommandSender sender, String path, TagResolver ...tagResolvers)
    {
        return getLanguage(locale).getPlaceholderComponentList(toPlayer(sender), path, tagResolvers);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderComponentList(player, path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderComponentList(player, path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getPlaceholderComponentList(toPlayer(sender), path, textReplacements);
    }

    public List<Component> getLegacyComponentList(String path)
    {
        return defaultLanguage.getLegacyComponentList(path);
    }

    public List<Component> getLegacyComponentList(String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyComponentList(path, textReplacements);
    }

    public List<Component> getLegacyComponentList(@Nullable Locale locale, String path)
    {
        return getLanguage(locale).getLegacyComponentList(path);
    }

    public List<Component> getLegacyComponentList(@Nullable Locale locale, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyComponentList(path, textReplacements);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Player player, String path)
    {
        return defaultLanguage.getLegacyPlaceholderComponentList(player, path);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable OfflinePlayer player, String path)
    {
        return defaultLanguage.getLegacyPlaceholderComponentList(player, path);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable CommandSender sender, String path)
    {
        return defaultLanguage.getLegacyPlaceholderComponentList(toPlayer(sender), path);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderComponentList(player, path, textReplacements);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderComponentList(player, path, textReplacements);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return defaultLanguage.getLegacyPlaceholderComponentList(toPlayer(sender), path, textReplacements);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderComponentList(player, path);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderComponentList(player, path);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Locale locale, @Nullable CommandSender sender, String path)
    {
        return getLanguage(locale).getLegacyPlaceholderComponentList(toPlayer(sender), path);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderComponentList(player, path, textReplacements);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderComponentList(player, path, textReplacements);
    }

    public List<Component> getLegacyPlaceholderComponentList(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement ...textReplacements)
    {
        return getLanguage(locale).getLegacyPlaceholderComponentList(toPlayer(sender), path, textReplacements);
    }

    public static String reduceString(List<String> stringList)
    {
        return stringList.stream().reduce(I18n::reduceString).orElse("");
    }

    public static String reduceString(String s1, String s2)
    {
        return s1 + "\n" + s2;
    }

    public static Component reduceComponent(List<Component> componentList)
    {
        return componentList.stream().reduce(I18n::reduceComponent).orElse(Component.empty());
    }

    public static Component reduceComponent(Component c1, Component c2)
    {
        return c1.append(Component.newline()).append(c2);
    }

    public static String applyTextReplacements(String string, TextReplacement ...textReplacements)
    {
        String result = string;
        for (TextReplacement replacement : textReplacements)
            result = replacement.replace(result);
        return result;
    }

    public static @Nullable Player toPlayer(@Nullable CommandSender sender)
    {
        if (sender instanceof Player player)
            return player;
        return null;
    }

    public static @Nullable Locale toLocale(@Nullable CommandSender sender)
    {
        if (sender instanceof Player player)
            return player.locale();
        return null;
    }

}
