package me.wyne.wutils.i18n.language;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import ru.vyarus.yaml.updater.YamlUpdater;
import ru.vyarus.yaml.updater.report.UpdateReport;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BaseLanguage implements Language {

    private final Logger log;

    private final String languageCode;
    private final Locale locale;

    private final File languageFile;
    private final FileConfiguration strings;
    private final Map<String, String> stringMap = new HashMap<>();

    public BaseLanguage(File languageFile, Logger log) {
        this.log = log;
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.locale = new Locale(languageCode);
        this.languageFile = languageFile;
        this.strings = YamlConfiguration.loadConfiguration(languageFile);
        strings.getKeys(false).stream()
                .filter(strings::isString)
                .forEach(path -> stringMap.put(path, strings.getString(path)));
    }

    public BaseLanguage(Language defaultLanguage, File languageFile, Logger log) {
        this.log = log;
        mergeDefaultStrings(defaultLanguage, languageFile);
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.locale = new Locale(languageCode);
        this.languageFile = languageFile;
        this.strings = YamlConfiguration.loadConfiguration(languageFile);
        strings.getKeys(false).stream()
                .filter(strings::isString)
                .forEach(path -> stringMap.put(path, strings.getString(path)));
    }

    private void mergeDefaultStrings(Language defaultLanguage, File languageFile) {
        if (defaultLanguage.getLanguageFile().length() == 0)
            return;
        log.info("Searching for missing strings in {}", languageFile.getName());
        UpdateReport report = YamlUpdater.create(languageFile, defaultLanguage.getLanguageFile())
                .backup(false)
                .update();
        if (report.isConfigChanged())
            log.info("Merged missing strings to {}", languageFile.getName());
        else
            log.info("{} is up to date", languageFile.getName());
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
    public FileConfiguration getStrings() {
        return strings;
    }

    @Override
    public Map<String, String> getStringMap() {
        return stringMap;
    }

    @Override
    public boolean contains(String path) {
        return strings.contains(path);
    }

}
