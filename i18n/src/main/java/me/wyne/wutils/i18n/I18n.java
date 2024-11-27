package me.wyne.wutils.i18n;

import me.wyne.wutils.i18n.language.BaseLanguage;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.BaseInterpreter;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreter;
import me.wyne.wutils.i18n.language.interpretation.LegacyInterpreter;
import me.wyne.wutils.i18n.language.interpretation.StringInterpreter;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.EmptyValidator;
import net.kyori.adventure.text.Component;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
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
    public LogWrapper log = new LogWrapper();

    private final Map<String, Language> languageMap = new HashMap<>();
    private Language defaultLanguage;
    private Language defaultResourceLanguage;

    private StringInterpreter stringInterpreter = new BaseInterpreter(new EmptyValidator());
    private ComponentInterpreter componentInterpreter = new LegacyInterpreter(new EmptyValidator());

    static {
        Configurator.setLevel(LogManager.getLogger("ru.vyarus"), Level.WARN);
    }

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

    public void loadLanguage(File languageFile)
    {
        if (languageMap.containsKey(FilenameUtils.removeExtension(languageFile.getName())))
            return;
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new BaseLanguage(defaultResourceLanguage, languageFile, log));
        log.info("Loaded " + FilenameUtils.removeExtension(languageFile.getName()) + " language");
    }

    public void loadLanguage(String languageResourcePath, InputStream languageResourceStream, File dataFolder)
    {
        File languageResource = new File(dataFolder, "defaults/" + languageResourcePath);
        File languageFile = new File(dataFolder, languageResourcePath);
        try {
            FileUtils.copyInputStreamToFile(languageResourceStream, languageResource);
        } catch (IOException e) {
            log.exception("An exception occurred trying to load " + languageResourcePath + " language", e); 
        }
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new BaseLanguage(new BaseLanguage(languageResource, log), languageFile, log));
        log.info("Loaded " + FilenameUtils.removeExtension(languageFile.getName()) + " language");
    }

    public void loadLanguage(String languageResourcePath, JavaPlugin plugin)
    {
        plugin.saveResource(languageResourcePath, false);
        File languageResource = new File(plugin.getDataFolder(), "defaults/" + languageResourcePath);
        File languageFile = new File(plugin.getDataFolder(), languageResourcePath);
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(languageResourcePath), languageResource);
        } catch (IOException e) {
            log.exception("An exception occurred trying to load " + languageResourcePath + " language", e);
        }
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new BaseLanguage(new BaseLanguage(languageResource, log), languageFile, log));
        log.info("Loaded " + FilenameUtils.removeExtension(languageFile.getName()) + " language");
    }

    public void loadLanguages(JavaPlugin plugin)
    {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists())
            langFolder.mkdirs();

        for (File file : langFolder.listFiles())
        {
            loadLanguage(file);
        }
    }

    public void loadLanguages(File langFolder)
    {
        if (!langFolder.exists())
            langFolder.mkdirs();

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
            defaultResourceLanguage = new BaseLanguage(languageResource, log);
        } catch (IOException e) {
            log.exception("An exception occurred trying to load default language from resources", e);
        }
    }

    public void loadDefaultResourceLanguage(File dataFolder, JavaPlugin plugin, Function<String, InputStream> resourceProvider)
    {
        File configResource = new File(plugin.getDataFolder(),  "defaults/config.yml");
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource("config.yml"), configResource);
            YamlConfiguration pluginConfig = YamlConfiguration.loadConfiguration(configResource);

            if (!pluginConfig.contains("lang"))
                return;

            File languageResource = new File(dataFolder, "defaults/lang/" + pluginConfig.getString("lang"));
            FileUtils.copyInputStreamToFile(resourceProvider.apply("lang/" + pluginConfig.getString("lang")), languageResource);
            defaultResourceLanguage = new BaseLanguage(languageResource, log);
        } catch (IOException e) {
            log.exception("An exception occurred trying to load default plugin language", e);
        }
    }

    @Nullable
    public String getDefaultLanguageCode(JavaPlugin plugin)
    {
        if (!plugin.getConfig().contains("lang", true))
        {
            log.warn("Plugin config doesn't contain default language path");
            log.warn("Absence of default language may and will cause issues");
            return null;
        }

        return FilenameUtils.removeExtension(plugin.getConfig().getString("lang"));
    }

    public void setDefaultLanguage(@Nullable File languageFile)
    {
        if (languageFile == null || !languageFile.exists())
        {
            log.error("Couldn't set default language to " + (languageFile != null ? languageFile.getName() : "null"));
            if (defaultLanguage == null)
            {
                log.warn("Will try to get language file from plugins resources");
                if (defaultResourceLanguage != null)
                {
                    defaultLanguage = defaultResourceLanguage;
                    log.warn("Using " + defaultResourceLanguage.getLanguageCode() + " as default language");
                    return;
                }
                log.warn("Couldn't get language file from plugin's resources");
            }
            return;
        }

        defaultLanguage = defaultResourceLanguage != null
                ? new BaseLanguage(defaultResourceLanguage, languageFile, log)
                : new BaseLanguage(languageFile, log);
        log.info("Default language is set to " + defaultLanguage.getLanguageCode());
    }

    public void setDefaultLanguage(String languageCode)
    {
        if (!languageMap.containsKey(languageCode))
        {
            log.error("Couldn't set default language to " + languageCode);
            if (defaultLanguage == null)
            {
                log.warn("Will try to get language file from plugins resources");
                if (defaultResourceLanguage != null)
                {
                    defaultLanguage = defaultResourceLanguage;
                    log.warn("Using " + defaultResourceLanguage.getLanguageCode() + " as default language");
                    return;
                }
                log.warn("Couldn't get language file from plugin's resources");
            }
            return;
        }

        defaultLanguage = languageMap.get(languageCode);
        log.info("Default language is set to " + defaultLanguage.getLanguageCode());
    }

    public void setStringInterpreter(StringInterpreter stringInterpreter) {
        this.stringInterpreter = stringInterpreter;
    }

    public void setComponentInterpreter(ComponentInterpreter componentInterpreter) {
        this.componentInterpreter = componentInterpreter;
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

    public StringInterpreter string() {
        return stringInterpreter;
    }

    public ComponentInterpreter component() {
        return componentInterpreter;
    }

    public String getString(@Nullable Locale locale, String path) {
        return string().getString(getLanguage(locale), path);
    }

    public String getString(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        return string().getString(getLanguage(locale), path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path) {
        return string().getPlaceholderString(getLanguage(locale), player, path);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return string().getPlaceholderString(getLanguage(locale), player, path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        return string().getPlaceholderString(getLanguage(locale), player, path);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return string().getPlaceholderString(getLanguage(locale), player, path, textReplacements);
    }

    public List<String> getStringList(@Nullable Locale locale, String path) {
        return string().getStringList(getLanguage(locale), path);
    }

    public List<String> getStringList(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        return string().getStringList(getLanguage(locale), path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path) {
        return string().getPlaceholderStringList(getLanguage(locale), player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return string().getPlaceholderStringList(getLanguage(locale), player, path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        return string().getPlaceholderStringList(getLanguage(locale), player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return string().getPlaceholderStringList(getLanguage(locale), player, path, textReplacements);
    }

    public Component getComponent(@Nullable Locale locale, String path) {
        return component().getComponent(getLanguage(locale), path);
    }

    public Component getComponent(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        return component().getComponent(getLanguage(locale), path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path) {
        return component().getPlaceholderComponent(getLanguage(locale), player, path);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return component().getPlaceholderComponent(getLanguage(locale), player, path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        return component().getPlaceholderComponent(getLanguage(locale), player, path);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return component().getPlaceholderComponent(getLanguage(locale), player, path, textReplacements);
    }

    public List<Component> getComponentList(@Nullable Locale locale, String path) {
        return component().getComponentList(getLanguage(locale), path);
    }

    public List<Component> getComponentList(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        return component().getComponentList(getLanguage(locale), path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path) {
        return component().getPlaceholderComponentList(getLanguage(locale), player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        return component().getPlaceholderComponentList(getLanguage(locale), player, path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        return component().getPlaceholderComponentList(getLanguage(locale), player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        return component().getPlaceholderComponentList(getLanguage(locale), player, path, textReplacements);
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
