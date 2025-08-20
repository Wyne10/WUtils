package me.wyne.wutils.i18n;

import me.wyne.wutils.i18n.language.BaseLanguage;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.component.*;
import me.wyne.wutils.i18n.language.interpretation.*;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.EmptyValidator;
import net.kyori.adventure.text.Component;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class I18n {

    public static I18n global = new I18n();
    public ComponentAudience audiences = new NativeComponentAudience();
    public Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, Language> languageMap = new HashMap<>();
    private Language defaultLanguage;
    private Language defaultResourceLanguage;

    private StringInterpreter stringInterpreter = new BaseInterpreter(new EmptyValidator());
    private ComponentInterpreter componentInterpreter = new LegacyInterpreter(new EmptyValidator());

    public boolean usePlayerLanguage = true;
    public static final boolean IS_PLAIN_TEXT_UNAVAILABLE = Bukkit.getVersion().contains("1.16");

    static {
        try {
            Configurator.setLevel("ru.vyarus", Level.WARN);
            Configurator.setLevel(LogManager.getLogger("ru.vyarus"), Level.WARN);
        } catch (NoSuchMethodError ignored) {}
    }

    public I18n() {}

    public I18n(File defaultLanguageFile) {
        setDefaultLanguage(defaultLanguageFile);
    }

    public I18n(JavaPlugin plugin) {
        loadDefaultResourceLanguage(plugin);
        loadLanguages(plugin);
        setDefaultLanguage(getDefaultLanguageCode(plugin));
    }

    public void loadLanguage(File languageFile) {
        if (languageMap.containsKey(FilenameUtils.removeExtension(languageFile.getName())))
            return;
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new BaseLanguage(defaultResourceLanguage, languageFile, log));
        log.info("Loaded {} language", FilenameUtils.removeExtension(languageFile.getName()));
    }

    public void loadLanguage(String languageResourcePath, InputStream languageResourceStream, File dataFolder) {
        File languageResource = new File(dataFolder, "defaults/" + languageResourcePath);
        File languageFile = new File(dataFolder, languageResourcePath);
        try {
            FileUtils.copyInputStreamToFile(languageResourceStream, languageResource);
        } catch (IOException e) {
            log.error("An exception occurred trying to load {} language", languageResourcePath, e);
        }
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new BaseLanguage(new BaseLanguage(languageResource, log), languageFile, log));
        log.info("Loaded {} language", FilenameUtils.removeExtension(languageFile.getName()));
    }

    public void loadLanguage(String languageResourcePath, JavaPlugin plugin) {
        File languageFile = new File(plugin.getDataFolder(), languageResourcePath);
        if (!languageFile.exists())
            plugin.saveResource(languageResourcePath, false);
        File languageResource = new File(plugin.getDataFolder(), "defaults/" + languageResourcePath);
        try {
            FileUtils.copyInputStreamToFile(plugin.getResource(languageResourcePath), languageResource);
        } catch (IOException e) {
            log.error("An exception occurred trying to load {} language", languageResourcePath, e);
        }
        languageMap.put(FilenameUtils.removeExtension(languageFile.getName()), new BaseLanguage(new BaseLanguage(languageResource, log), languageFile, log));
        log.info("Loaded {} language", FilenameUtils.removeExtension(languageFile.getName()));
    }

    public void loadLanguages(JavaPlugin plugin) {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists())
            langFolder.mkdirs();

        for (File file : langFolder.listFiles()) {
            if (!file.isFile()) continue;
            loadLanguage(file);
        }
    }

    public void loadLanguages(File langFolder) {
        if (!langFolder.exists())
            langFolder.mkdirs();

        for (File file : langFolder.listFiles()) {
            if (!file.isFile()) continue;
            loadLanguage(file);
        }
    }

    public void loadDefaultResourceLanguage(JavaPlugin plugin) {
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
            log.error("An exception occurred trying to load default language from resources", e);
        }
    }

    public void loadDefaultResourceLanguage(File dataFolder, JavaPlugin plugin, Function<String, InputStream> resourceProvider) {
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
            log.error("An exception occurred trying to load default plugin language", e);
        }
    }

    @Nullable
    public String getDefaultLanguageCode(JavaPlugin plugin) {
        if (!plugin.getConfig().contains("lang", true)) {
            log.warn("Plugin config doesn't contain default language path");
            log.warn("Absence of default language may and will cause issues");
            return null;
        }

        return FilenameUtils.removeExtension(plugin.getConfig().getString("lang"));
    }

    public void setDefaultLanguage(@Nullable File languageFile) {
        if (languageFile == null || !languageFile.exists()) {
            log.error("Couldn't set default language to {}", languageFile != null ? languageFile.getName() : "null");
            if (defaultLanguage == null) {
                log.warn("Will try to get language file from plugins resources");
                if (defaultResourceLanguage != null) {
                    defaultLanguage = defaultResourceLanguage;
                    log.warn("Using {} as default language", defaultResourceLanguage.getLanguageCode());
                    return;
                }
                log.warn("Couldn't get language file from plugin's resources");
            }
            return;
        }

        defaultLanguage = defaultResourceLanguage != null
                ? new BaseLanguage(defaultResourceLanguage, languageFile, log)
                : new BaseLanguage(languageFile, log);
        log.info("Default language is set to {}", defaultLanguage.getLanguageCode());
    }

    public void setDefaultLanguage(String languageCode) {
        if (!languageMap.containsKey(languageCode)) {
            log.error("Couldn't set default language to {}", languageCode);
            if (defaultLanguage == null) {
                log.warn("Will try to get language file from plugins resources");
                if (defaultResourceLanguage != null) {
                    defaultLanguage = defaultResourceLanguage;
                    log.warn("Using {} as default language", defaultResourceLanguage.getLanguageCode());
                    return;
                }
                log.warn("Couldn't get language file from plugin's resources");
            }
            return;
        }

        defaultLanguage = languageMap.get(languageCode);
        log.info("Default language is set to {}", defaultLanguage.getLanguageCode());
    }

    public void setStringInterpreter(StringInterpreter stringInterpreter) {
        this.stringInterpreter = stringInterpreter;
    }

    public void setComponentInterpreter(ComponentInterpreter componentInterpreter) {
        this.componentInterpreter = componentInterpreter;
    }

    public void clearLanguageMap() {
        languageMap.clear();
    }

    public Map<String, Language> getLanguageMap() {
        return languageMap;
    }

    public Language getLanguage(@Nullable Locale locale) {
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

    public LocalizedString getString(String path) {
        if (defaultLanguage.getStrings().isList(path))
            return lsd(path, reduceString(getStringList(path)));
        return lsd(path, string().getString(defaultLanguage, path));
    }

    public LocalizedString getString(@Nullable Locale locale, String path) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return ls(language, path, reduceString(getStringList(locale, path)));
        return ls(language, path, string().getString(language, path));
    }

    public LocalizedString getString(String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return lsd(path, reduceString(getStringList(path, textReplacements)));
        return lsd(path, string().getString(defaultLanguage, path, textReplacements));
    }

    public LocalizedString getString(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return ls(language, path, reduceString(getStringList(locale, path, textReplacements)));
        return ls(language, path, string().getString(language, path, textReplacements));
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable Player player, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return plsd(path, reduceString(getPlaceholderStringList(player, path)), player);
        return plsd(path, string().getPlaceholderString(defaultLanguage, player, path), player);
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return pls(language, path, reduceString(getPlaceholderStringList(locale, player, path)), player);
        return pls(language, path, string().getPlaceholderString(language, player, path), player);
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable Player player, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return plsd(path, reduceString(getPlaceholderStringList(player, path, textReplacements)), player);
        return plsd(path, string().getPlaceholderString(defaultLanguage, player, path, textReplacements), player);
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return pls(language, path, reduceString(getPlaceholderStringList(locale, player, path, textReplacements)), player);
        return pls(language, path, string().getPlaceholderString(language, player, path, textReplacements), player);
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return plsd(path, reduceString(getPlaceholderStringList(player, path)), player);
        return plsd(path, string().getPlaceholderString(defaultLanguage, player, path), player);
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return pls(language, path, reduceString(getPlaceholderStringList(locale, player, path)), player);
        return pls(language, path, string().getPlaceholderString(language, player, path), player);
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return plsd(path, reduceString(getPlaceholderStringList(player, path, textReplacements)), player);
        return plsd(path, string().getPlaceholderString(defaultLanguage, player, path, textReplacements), player);
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return pls(language, path, reduceString(getPlaceholderStringList(locale, player, path, textReplacements)), player);
        return pls(language, path, string().getPlaceholderString(language, player, path, textReplacements), player);
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return plsd(path, reduceString(getPlaceholderStringList(sender, path)), I18n.toPlayer(sender));
        return plsd(path, string().getPlaceholderString(defaultLanguage, I18n.toPlayer(sender), path), I18n.toPlayer(sender));
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable Locale locale, @Nullable CommandSender sender, String path) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return pls(language, path, reduceString(getPlaceholderStringList(locale, sender, path)), I18n.toPlayer(sender));
        return pls(language, path, string().getPlaceholderString(language, I18n.toPlayer(sender), path), I18n.toPlayer(sender));
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return plsd(path, reduceString(getPlaceholderStringList(sender, path, textReplacements)), I18n.toPlayer(sender));
        return plsd(path, string().getPlaceholderString(defaultLanguage, I18n.toPlayer(sender), path, textReplacements), I18n.toPlayer(sender));
    }

    public PlaceholderLocalizedString getPlaceholderString(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return pls(language, path, reduceString(getPlaceholderStringList(locale, sender, path, textReplacements)), I18n.toPlayer(sender));
        return pls(language, path, string().getPlaceholderString(language, I18n.toPlayer(sender), path, textReplacements), I18n.toPlayer(sender));
    }

    public List<LocalizedString> getStringList(String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getString(path));
        return string().getStringList(defaultLanguage, path).stream().map(string -> lsd(path, string)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<LocalizedString> getStringList(@Nullable Locale locale, String path) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getString(locale, path));
        return string().getStringList(language, path).stream().map(string -> ls(language, path, string)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<LocalizedString> getStringList(String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getString(path, textReplacements));
        return string().getStringList(defaultLanguage, path, textReplacements).stream().map(string -> lsd(path, string)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<LocalizedString> getStringList(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getString(locale, path, textReplacements));
        return string().getStringList(language, path, textReplacements).stream().map(string -> ls(language, path, string)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(player, path));
        return string().getPlaceholderStringList(defaultLanguage, player, path).stream().map(string -> plsd(path, string, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderString(locale, player, path));
        return string().getPlaceholderStringList(language, player, path).stream().map(string -> pls(language, path, string, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Player player, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(player, path, textReplacements));
        return string().getPlaceholderStringList(defaultLanguage, player, path, textReplacements).stream().map(string -> plsd(path, string, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderString(locale, player, path, textReplacements));
        return string().getPlaceholderStringList(language, player, path, textReplacements).stream().map(string -> pls(language, path, string, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(player, path));
        return string().getPlaceholderStringList(defaultLanguage, player, path).stream().map(string -> plsd(path, string, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderString(locale, player, path));
        return string().getPlaceholderStringList(language, player, path).stream().map(string -> pls(language, path, string, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(player, path, textReplacements));
        return string().getPlaceholderStringList(defaultLanguage, player, path, textReplacements).stream().map(string -> plsd(path, string, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderString(locale, player, path, textReplacements));
        return string().getPlaceholderStringList(language, player, path, textReplacements).stream().map(string -> pls(language, path, string, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(sender, path));
        return string().getPlaceholderStringList(defaultLanguage, I18n.toPlayer(sender), path).stream().map(string -> plsd(path, string, I18n.toPlayer(sender))).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Locale locale, @Nullable CommandSender sender, String path) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderString(locale, sender, path));
        return string().getPlaceholderStringList(language, I18n.toPlayer(sender), path).stream().map(string -> pls(language, path, string, I18n.toPlayer(sender))).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderString(sender, path, textReplacements));
        return string().getPlaceholderStringList(defaultLanguage, I18n.toPlayer(sender), path, textReplacements).stream().map(string -> plsd(path, string, I18n.toPlayer(sender))).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedString> getPlaceholderStringList(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderString(locale, sender, path, textReplacements));
        return string().getPlaceholderStringList(language, I18n.toPlayer(sender), path, textReplacements).stream().map(string -> pls(language, path, string, I18n.toPlayer(sender))).collect(Collectors.toCollection(ArrayList::new));
    }

    public LocalizedComponent getComponent(String path) {
        if (defaultLanguage.getStrings().isList(path))
            return lcd(path, reduceComponent(getComponentList(path)));
        return lcd(path, component().getComponent(defaultLanguage, path));
    }

    public LocalizedComponent getComponent(@Nullable Locale locale, String path) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return lc(language, path, reduceComponent(getComponentList(locale, path)));
        return lc(language, path, component().getComponent(language, path));
    }

    public LocalizedComponent getComponent(String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return lcd(path, reduceComponent(getComponentList(path, textReplacements)));
        return lcd(path, component().getComponent(defaultLanguage, path, textReplacements));
    }

    public LocalizedComponent getComponent(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return lc(language, path, reduceComponent(getComponentList(locale, path, textReplacements)));
        return lc(language, path, component().getComponent(language, path, textReplacements));
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return plcd(path, reduceComponent(getPlaceholderComponentList(player, path)), player);
        return plcd(path, component().getPlaceholderComponent(defaultLanguage, player, path), player);
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return plc(language, path, reduceComponent(getPlaceholderComponentList(locale, player, path)), player);
        return plc(language, path, component().getPlaceholderComponent(language, player, path), player);
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Player player, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return plcd(path, reduceComponent(getPlaceholderComponentList(player, path, textReplacements)), player);
        return plcd(path, component().getPlaceholderComponent(defaultLanguage, player, path, textReplacements), player);
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return plc(language, path, reduceComponent(getPlaceholderComponentList(locale, player, path, textReplacements)), player);
        return plc(language, path, component().getPlaceholderComponent(language, player, path, textReplacements), player);
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return plcd(path, reduceComponent(getPlaceholderComponentList(player, path)), player);
        return plcd(path, component().getPlaceholderComponent(defaultLanguage, player, path), player);
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return plc(language, path, reduceComponent(getPlaceholderComponentList(locale, player, path)), player);
        return plc(language, path, component().getPlaceholderComponent(language, player, path), player);
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return plcd(path, reduceComponent(getPlaceholderComponentList(player, path, textReplacements)), player);
        return plcd(path, component().getPlaceholderComponent(defaultLanguage, player, path, textReplacements), player);
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return plc(language, path, reduceComponent(getPlaceholderComponentList(locale, player, path, textReplacements)), player);
        return plc(language, path, component().getPlaceholderComponent(language, player, path, textReplacements), player);
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender, String path) {
        if (defaultLanguage.getStrings().isList(path))
            return plcd(path, reduceComponent(getPlaceholderComponentList(sender, path)), I18n.toPlayer(sender));
        return plcd(path, component().getPlaceholderComponent(defaultLanguage, I18n.toPlayer(sender), path), I18n.toPlayer(sender));
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Locale locale, @Nullable CommandSender sender, String path) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return plc(language, path, reduceComponent(getPlaceholderComponentList(locale, sender, path)), I18n.toPlayer(sender));
        return plc(language, path, component().getPlaceholderComponent(language, I18n.toPlayer(sender), path), I18n.toPlayer(sender));
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (defaultLanguage.getStrings().isList(path))
            return plcd(path, reduceComponent(getPlaceholderComponentList(sender, path, textReplacements)), I18n.toPlayer(sender));
        return plcd(path, component().getPlaceholderComponent(defaultLanguage, I18n.toPlayer(sender), path, textReplacements), I18n.toPlayer(sender));
    }

    public PlaceholderLocalizedComponent getPlaceholderComponent(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (language.getStrings().isList(path))
            return plc(language, path, reduceComponent(getPlaceholderComponentList(locale, sender, path, textReplacements)), I18n.toPlayer(sender));
        return plc(language, path, component().getPlaceholderComponent(language, I18n.toPlayer(sender), path, textReplacements), I18n.toPlayer(sender));
    }

    public List<LocalizedComponent> getComponentList(String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getComponent(path));
        return component().getComponentList(defaultLanguage, path).stream().map(component -> lcd(path, component)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<LocalizedComponent> getComponentList(@Nullable Locale locale, String path) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getComponent(locale, path));
        return component().getComponentList(language, path).stream().map(component -> lc(language, path, component)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<LocalizedComponent> getComponentList(String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getComponent(path, textReplacements));
        return component().getComponentList(defaultLanguage, path, textReplacements).stream().map(component -> lcd(path, component)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<LocalizedComponent> getComponentList(@Nullable Locale locale, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getComponent(locale, path, textReplacements));
        return component().getComponentList(language, path, textReplacements).stream().map(component -> lc(language, path, component)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(player, path));
        return component().getPlaceholderComponentList(defaultLanguage, player, path).stream().map(component -> plcd(path, component, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, player, path));
        return component().getPlaceholderComponentList(language, player, path).stream().map(component -> plc(language, path, component, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Player player, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(player, path, textReplacements));
        return component().getPlaceholderComponentList(defaultLanguage, player, path, textReplacements).stream().map(component -> plcd(path, component, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Locale locale, @Nullable Player player, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, player, path, textReplacements));
        return component().getPlaceholderComponentList(language, player, path, textReplacements).stream().map(component -> plc(language, path, component, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(player, path));
        return component().getPlaceholderComponentList(defaultLanguage, player, path).stream().map(component -> plcd(path, component, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, player, path));
        return component().getPlaceholderComponentList(language, player, path).stream().map(component -> plc(language, path, component, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(player, path, textReplacements));
        return component().getPlaceholderComponentList(defaultLanguage, player, path, textReplacements).stream().map(component -> plcd(path, component, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Locale locale, @Nullable OfflinePlayer player, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, player, path, textReplacements));
        return component().getPlaceholderComponentList(language, player, path, textReplacements).stream().map(component -> plc(language, path, component, player)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender, String path) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(sender, path));
        return component().getPlaceholderComponentList(defaultLanguage, I18n.toPlayer(sender), path).stream().map(component -> plcd(path, component, I18n.toPlayer(sender))).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Locale locale, @Nullable CommandSender sender, String path) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, sender, path));
        return component().getPlaceholderComponentList(language, I18n.toPlayer(sender), path).stream().map(component -> plc(language, path, component, I18n.toPlayer(sender))).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        if (!defaultLanguage.getStrings().isList(path))
            return List.of(getPlaceholderComponent(sender, path, textReplacements));
        return component().getPlaceholderComponentList(defaultLanguage, I18n.toPlayer(sender), path, textReplacements).stream().map(component -> plcd(path, component, I18n.toPlayer(sender))).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PlaceholderLocalizedComponent> getPlaceholderComponentList(@Nullable Locale locale, @Nullable CommandSender sender, String path, TextReplacement... textReplacements) {
        Language language = getLanguage(locale);
        if (!language.getStrings().isList(path))
            return List.of(getPlaceholderComponent(locale, sender, path, textReplacements));
        return component().getPlaceholderComponentList(language, I18n.toPlayer(sender), path, textReplacements).stream().map(component -> plc(language, path, component, I18n.toPlayer(sender))).collect(Collectors.toCollection(ArrayList::new));
    }

    private LocalizedString ls(Language language, String path, String string) {
        return new LocalizedString(string(), language, path, string);
    }

    private PlaceholderLocalizedString pls(Language language, String path, String string, @Nullable OfflinePlayer player) {
        return new PlaceholderLocalizedString(string(), language, path, string, player);
    }

    private LocalizedString lsd(String path, String string) {
        return new LocalizedString(string(), defaultLanguage, path, string);
    }

    private PlaceholderLocalizedString plsd(String path, String string, @Nullable OfflinePlayer player) {
        return new PlaceholderLocalizedString(string(), defaultLanguage, path, string, player);
    }

    private LocalizedComponent lc(Language language, String path, Component component) {
        return new LocalizedComponent(component(), language, path, component);
    }

    private PlaceholderLocalizedComponent plc(Language language, String path, Component component, @Nullable OfflinePlayer player) {
        return new PlaceholderLocalizedComponent(component(), language, path, component, player);
    }

    private LocalizedComponent lcd(String path, Component component) {
        return new LocalizedComponent(component(), defaultLanguage, path, component);
    }

    private PlaceholderLocalizedComponent plcd(String path, Component component, @Nullable OfflinePlayer player) {
        return new PlaceholderLocalizedComponent(component(), defaultLanguage, path, component, player);
    }

    public static String reduceRawString(Collection<String> stringList) {
        return stringList.stream().reduce(I18n::reduceRawString).orElse("");
    }

    public static String reduceRawString(String s1, String s2) {
        return s1 + "\n" + s2;
    }

    public static Component reduceRawComponent(Collection<Component> componentList) {
        return componentList.stream().reduce(I18n::reduceRawComponent).orElse(Component.empty());
    }

    public static Component reduceRawComponent(Component c1, Component c2) {
        return c1.append(Component.newline()).append(c2);
    }

    public static <T extends LocalizedString> String reduceString(Collection<T> stringList) {
        return stringList.stream().map(T::get).reduce(I18n::reduceRawString).orElse("");
    }

    public static <T extends LocalizedString> String reduceString(T s1, T s2) {
        return s1.get() + "\n" + s2.get();
    }

    public static <T extends LocalizedComponent> Component reduceComponent(Collection<T> componentList) {
        return componentList.stream().map(T::get).reduce(I18n::reduceRawComponent).orElse(Component.empty());
    }

    public static <T extends LocalizedComponent> Component reduceComponent(T c1, T c2) {
        return c1.get().append(Component.newline()).append(c2.get());
    }

    public static List<String> ofRawStrings(Collection<String> paths, Function<String, String> operation) {
        return paths.stream()
                .map(operation)
                .toList();
    }

    public static List<Component> ofRawComponents(Collection<String> paths, Function<String, Component> operation) {
        return paths.stream()
                .map(operation)
                .toList();
    }

    public static List<LocalizedString> ofStrings(Collection<String> paths, Function<String, LocalizedString> operation) {
        return paths.stream()
                .map(operation)
                .toList();
    }

    public static List<LocalizedComponent> ofComponents(Collection<String> paths, Function<String, LocalizedComponent> operation) {
        return paths.stream()
                .map(operation)
                .toList();
    }

    public static List<Component> asComponents(Collection<LocalizedComponent> localizedComponents) {
        return localizedComponents.stream().map(LocalizedComponent::asComponent).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<LocalizedComponent> applyComponentReplacements(Collection<LocalizedComponent> localizedComponents, ComponentReplacement... replacements) {
        return localizedComponents.stream().map(lc -> lc.replace(replacements)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static String applyTextReplacements(String string, TextReplacement ...textReplacements) {
        String result = string;
        for (TextReplacement replacement : textReplacements)
            result = replacement.replace(result);
        return result;
    }

    public static @Nullable <T> Player toPlayer(@Nullable T something) {
        if (something instanceof Player player)
            return player;
        return null;
    }

    public static @Nullable <T> Locale toLocale(@Nullable T something) {
        if (something instanceof Player player)
            return player.locale();
        return null;
    }

}
