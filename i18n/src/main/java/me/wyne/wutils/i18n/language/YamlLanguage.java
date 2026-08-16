package me.wyne.wutils.i18n.language;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ru.vyarus.yaml.updater.YamlUpdater;
import ru.vyarus.yaml.updater.report.UpdateReport;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class YamlLanguage implements Language {

    private final Logger logger;

    private final String languageCode;
    private final Locale locale;

    private final File languageFile;
    private final LanguageStrings strings;
    private final Map<String, String> stringMap = new HashMap<>();

    public YamlLanguage(File languageFile, Logger logger) {
        this(null, languageFile, logger);
    }

    public YamlLanguage(@Nullable Language defaultLanguage, File languageFile, Logger logger) {
        this.logger = logger;
        mergeDefaultStrings(defaultLanguage, languageFile);
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.locale = new Locale(languageCode);
        this.languageFile = languageFile;
        ConfigurationSection section = YamlConfiguration.loadConfiguration(languageFile);
        this.strings = new YamlLanguageStrings(section);
        section.getKeys(false).stream()
                .filter(section::isString)
                .forEach(path -> stringMap.put(path, section.getString(path)));
    }

    private void mergeDefaultStrings(@Nullable Language defaultLanguage, File languageFile) {
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
    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public File getLanguageFile() {
        return languageFile;
    }

    @Override
    public LanguageStrings getStrings() {
        return strings;
    }

    @Override
    public Map<String, String> getStringMap() {
        return stringMap;
    }

}
