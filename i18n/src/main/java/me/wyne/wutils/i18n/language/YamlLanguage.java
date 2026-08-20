package me.wyne.wutils.i18n.language;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ru.vyarus.yaml.updater.YamlUpdater;
import ru.vyarus.yaml.updater.report.UpdateReport;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * {@link Language} backed by a YAML file, read with Bukkit's {@link YamlConfiguration}.
 *
 * <p>{@link #getStringMap()} is built from every string key in the file, at any depth, keyed by its full
 * dotted path — so {@code messages.welcome} resolves the same way through {@link #getStringMap()} as it
 * does through {@link #getStrings()}.</p>
 */
public class YamlLanguage implements Language {

    private final Logger logger;

    private final String languageCode;
    private final Locale locale;

    private final File languageFile;
    private final LanguageStrings strings;
    private final Map<String, String> stringMap = new HashMap<>();

    /** Equivalent to {@link #YamlLanguage(Language, File, Logger)} with no default language. */
    public YamlLanguage(@NotNull File languageFile, @NotNull Logger logger) {
        this(null, languageFile, logger);
    }

    /**
     * Loads YAML strings from {@code languageFile}, first back-filling any keys present in
     * {@code defaultLanguage}'s file but missing from this one via {@code yaml-config-updater} — which
     * mutates {@code languageFile} on disk. Skipped when {@code defaultLanguage} is {@code null} or its
     * file is empty.
     */
    public YamlLanguage(@Nullable Language defaultLanguage, @NotNull File languageFile, @NotNull Logger logger) {
        this.logger = logger;
        mergeDefaultStrings(defaultLanguage, languageFile);
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.locale = new Locale(languageCode);
        this.languageFile = languageFile;
        ConfigurationSection section = YamlConfiguration.loadConfiguration(languageFile);
        this.strings = new YamlLanguageStrings(section);
        section.getKeys(true).stream()
                .filter(section::isString)
                .forEach(path -> stringMap.put(path, section.getString(path)));
    }

    private void mergeDefaultStrings(@Nullable Language defaultLanguage, @NotNull File languageFile) {
        if (defaultLanguage == null)
            return;
        if (defaultLanguage.getLanguageFile().length() == 0)
            return;
        UpdateReport report = YamlUpdater.create(languageFile, defaultLanguage.getLanguageFile())
                .backup(false)
                .update();
        if (report.isConfigChanged())
            logger.debug("Merged missing strings to {}", languageFile.getName());
    }

    @Override
    public @NotNull String getLanguageCode() {
        return languageCode;
    }

    @Override
    public @NotNull Locale getLocale() {
        return locale;
    }

    @Override
    public @NotNull File getLanguageFile() {
        return languageFile;
    }

    @Override
    public @NotNull LanguageStrings getStrings() {
        return strings;
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull String> getStringMap() {
        return stringMap;
    }

}
