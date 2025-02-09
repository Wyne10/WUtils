package me.wyne.wutils.i18n;

import me.wyne.wutils.i18n.language.BaseLanguage;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.*;
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

    private boolean usePlayerLanguage = true;

    static {
        try {
            Configurator.setLevel(LogManager.getLogger("ru.vyarus"), Level.WARN);
        } catch (NoSuchMethodError ignored) {}
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

    public void setUsePlayerLanguage(boolean usePlayerLanguage) {
        this.usePlayerLanguage = usePlayerLanguage;
    }

    public boolean isUsePlayerLanguage() {
        return usePlayerLanguage;
    }

    public void clearLanguageMap() {
        languageMap.clear();
    }

    public Language getLanguage(@Nullable Locale locale)
    {
        if (!usePlayerLanguage)
            return defaultLanguage;
        if (locale == null)
            return defaultLanguage;
        if (locale.getLanguage().isEmpty())
            return defaultLanguage;
        if (!languageMap.containsKey(locale.getLanguage()))
            return defaultLanguage;
        return languageMap.get(locale.getLanguage());
    }

    public boolean contains(String path) {
        return defaultLanguage.contains(path);
    }

    public boolean contains(@Nullable Locale locale, String path) {
        return getLanguage(locale).contains(path);
    }

    public StringInterpreter string() {
        return stringInterpreter;
    }

    public ComponentInterpreter component() {
        return componentInterpreter;
    }

    public String getString(String path) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceString(getStringList(path));
        return string().getString(defaultLanguage, path);
    }

    public String getString(@Nullable Locale locale, String path) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceString(getStringList(locale, path));
        return string().getString(getLanguage(locale), path);
    }

    public String getString(String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceString(getStringList(path, textReplacements));
        return string().getString(defaultLanguage, path, textReplacements);
    }

    public String getString(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceString(getStringList(locale, path, textReplacements));
        return string().getString(getLanguage(locale), path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Player player, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceString(getPlaceholderStringList(player, path));
        return string().getPlaceholderString(defaultLanguage, player, path);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceString(getPlaceholderStringList(locale, player, path));
        return string().getPlaceholderString(getLanguage(locale), player, path);
    }

    public String getPlaceholderString(@Nullable Player player, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceString(getPlaceholderStringList(player, path, textReplacements));
        return string().getPlaceholderString(defaultLanguage, player, path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceString(getPlaceholderStringList(locale, player, path, textReplacements));
        return string().getPlaceholderString(getLanguage(locale), player, path, textReplacements);
    }

    public String getPlaceholderString(@Nullable OfflinePlayer player, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceString(getPlaceholderStringList(player, path));
        return string().getPlaceholderString(defaultLanguage, player, path);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceString(getPlaceholderStringList(locale, player, path));
        return string().getPlaceholderString(getLanguage(locale), player, path);
    }

    public String getPlaceholderString(@Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceString(getPlaceholderStringList(player, path, textReplacements));
        return string().getPlaceholderString(defaultLanguage, player, path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceString(getPlaceholderStringList(locale, player, path, textReplacements));
        return string().getPlaceholderString(getLanguage(locale), player, path, textReplacements);
    }

    public String getPlaceholderString(@Nullable CommandSender sender, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceString(getPlaceholderStringList(sender, path));
        return string().getPlaceholderString(defaultLanguage, I18n.toPlayer(sender), path);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable CommandSender sender, String path) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceString(getPlaceholderStringList(locale, sender, path));
        return string().getPlaceholderString(getLanguage(locale), I18n.toPlayer(sender), path);
    }

    public String getPlaceholderString(@Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceString(getPlaceholderStringList(sender, path, textReplacements));
        return string().getPlaceholderString(defaultLanguage, I18n.toPlayer(sender), path, textReplacements);
    }

    public String getPlaceholderString(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceString(getPlaceholderStringList(locale, sender, path, textReplacements));
        return string().getPlaceholderString(getLanguage(locale), I18n.toPlayer(sender), path, textReplacements);
    }

    public List<String> getStringList(String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getString(path));
        return string().getStringList(defaultLanguage, path);
    }

    public List<String> getStringList(@Nullable Locale locale, String path) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getString(locale, path));
        return string().getStringList(getLanguage(locale), path);
    }

    public List<String> getStringList(String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getString(path, textReplacements));
        return string().getStringList(defaultLanguage, path, textReplacements);
    }

    public List<String> getStringList(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getString(locale, path, textReplacements));
        return string().getStringList(getLanguage(locale), path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Player player, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(player, path));
        return string().getPlaceholderStringList(defaultLanguage, player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderString(locale, player, path));
        return string().getPlaceholderStringList(getLanguage(locale), player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable Player player, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(player, path, textReplacements));
        return string().getPlaceholderStringList(defaultLanguage, player, path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderString(locale, player, path, textReplacements));
        return string().getPlaceholderStringList(getLanguage(locale), player, path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable OfflinePlayer player, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(player, path));
        return string().getPlaceholderStringList(defaultLanguage, player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderString(locale, player, path));
        return string().getPlaceholderStringList(getLanguage(locale), player, path);
    }

    public List<String> getPlaceholderStringList(@Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(player, path, textReplacements));
        return string().getPlaceholderStringList(defaultLanguage, player, path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderString(locale, player, path, textReplacements));
        return string().getPlaceholderStringList(getLanguage(locale), player, path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable CommandSender sender, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(sender, path));
        return string().getPlaceholderStringList(defaultLanguage, I18n.toPlayer(sender), path);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable CommandSender sender, String path) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderString(locale, sender, path));
        return string().getPlaceholderStringList(getLanguage(locale), I18n.toPlayer(sender), path);
    }

    public List<String> getPlaceholderStringList(@Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(sender, path, textReplacements));
        return string().getPlaceholderStringList(defaultLanguage, I18n.toPlayer(sender), path, textReplacements);
    }

    public List<String> getPlaceholderStringList(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderString(locale, sender, path, textReplacements));
        return string().getPlaceholderStringList(getLanguage(locale), I18n.toPlayer(sender), path, textReplacements);
    }

    public Component getComponent(String path) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceComponent(getComponentList(path));
        return component().getComponent(defaultLanguage, path);
    }

    public Component getComponent(@Nullable Locale locale, String path) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceComponent(getComponentList(locale, path));
        return component().getComponent(getLanguage(locale), path);
    }

    public Component getComponent(String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceComponent(getComponentList(path, textReplacements));
        return component().getComponent(defaultLanguage, path, textReplacements);
    }

    public Component getComponent(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceComponent(getComponentList(locale, path, textReplacements));
        return component().getComponent(getLanguage(locale), path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Player player, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(player, path));
        return component().getPlaceholderComponent(defaultLanguage, player, path);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(locale, player, path));
        return component().getPlaceholderComponent(getLanguage(locale), player, path);
    }

    public Component getPlaceholderComponent(@Nullable Player player, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(player, path, textReplacements));
        return component().getPlaceholderComponent(defaultLanguage, player, path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(locale, player, path, textReplacements));
        return component().getPlaceholderComponent(getLanguage(locale), player, path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(player, path));
        return component().getPlaceholderComponent(defaultLanguage, player, path);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(locale, player, path));
        return component().getPlaceholderComponent(getLanguage(locale), player, path);
    }

    public Component getPlaceholderComponent(@Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(player, path, textReplacements));
        return component().getPlaceholderComponent(defaultLanguage, player, path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(locale, player, path, textReplacements));
        return component().getPlaceholderComponent(getLanguage(locale), player, path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable CommandSender sender, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(sender, path));
        return component().getPlaceholderComponent(defaultLanguage, I18n.toPlayer(sender), path);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable CommandSender sender, String path) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(locale, sender, path));
        return component().getPlaceholderComponent(getLanguage(locale), I18n.toPlayer(sender), path);
    }

    public Component getPlaceholderComponent(@Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(sender, path, textReplacements));
        return component().getPlaceholderComponent(defaultLanguage, I18n.toPlayer(sender), path, textReplacements);
    }

    public Component getPlaceholderComponent(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (getLanguage(locale).getStrings().isList(path))
            return reduceComponent(getPlaceholderComponentList(locale, sender, path, textReplacements));
        return component().getPlaceholderComponent(getLanguage(locale), I18n.toPlayer(sender), path, textReplacements);
    }

    public List<Component> getComponentList(String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getComponent(path));
        return component().getComponentList(defaultLanguage, path);
    }

    public List<Component> getComponentList(@Nullable Locale locale, String path) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getComponent(locale, path));
        return component().getComponentList(getLanguage(locale), path);
    }

    public List<Component> getComponentList(String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getComponent(path, textReplacements));
        return component().getComponentList(defaultLanguage, path, textReplacements);
    }

    public List<Component> getComponentList(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getComponent(locale, path, textReplacements));
        return component().getComponentList(getLanguage(locale), path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(player, path));
        return component().getPlaceholderComponentList(defaultLanguage, player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, player, path));
        return component().getPlaceholderComponentList(getLanguage(locale), player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Player player, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(player, path, textReplacements));
        return component().getPlaceholderComponentList(defaultLanguage, player, path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, player, path, textReplacements));
        return component().getPlaceholderComponentList(getLanguage(locale), player, path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(player, path));
        return component().getPlaceholderComponentList(defaultLanguage, player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, player, path));
        return component().getPlaceholderComponentList(getLanguage(locale), player, path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(player, path, textReplacements));
        return component().getPlaceholderComponentList(defaultLanguage, player, path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, player, path, textReplacements));
        return component().getPlaceholderComponentList(getLanguage(locale), player, path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable CommandSender sender, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(sender, path));
        return component().getPlaceholderComponentList(defaultLanguage, I18n.toPlayer(sender), path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable CommandSender sender, String path) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, sender, path));
        return component().getPlaceholderComponentList(getLanguage(locale), I18n.toPlayer(sender), path);
    }

    public List<Component> getPlaceholderComponentList(@Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(sender, path, textReplacements));
        return component().getPlaceholderComponentList(defaultLanguage, I18n.toPlayer(sender), path, textReplacements);
    }

    public List<Component> getPlaceholderComponentList(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (!getLanguage(locale).getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, sender, path, textReplacements));
        return component().getPlaceholderComponentList(getLanguage(locale), I18n.toPlayer(sender), path, textReplacements);
    }

    public static String reduceString(Collection<String> stringList)
    {
        return stringList.stream().reduce(I18n::reduceString).orElse("");
    }

    public static String reduceString(String s1, String s2)
    {
        return s1 + "\n" + s2;
    }

    public static Component reduceComponent(Collection<Component> componentList)
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
