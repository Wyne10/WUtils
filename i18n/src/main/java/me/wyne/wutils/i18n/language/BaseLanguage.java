package me.wyne.wutils.i18n.language;

import me.wyne.wutils.i18n.LogWrapper;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.vyarus.yaml.updater.YamlUpdater;
import ru.vyarus.yaml.updater.report.UpdateReport;

import java.io.File;

public class BaseLanguage implements Language {

    private final LogWrapper log;

    private final String languageCode;
    private final File languageFile;
    private final FileConfiguration strings;

    public BaseLanguage(File languageFile, LogWrapper log)
    {
        this.log = log;
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.languageFile = languageFile;
        this.strings = YamlConfiguration.loadConfiguration(languageFile);
    }

    public BaseLanguage(Language defaultLanguage, File languageFile, LogWrapper log)
    {
        this.log = log;
        mergeDefaultStrings(defaultLanguage, languageFile);
        this.languageCode = FilenameUtils.removeExtension(languageFile.getName());
        this.languageFile = languageFile;
        this.strings = YamlConfiguration.loadConfiguration(languageFile);
    }

    private void mergeDefaultStrings(Language defaultLanguage, File languageFile)
    {
        if (defaultLanguage.getLanguageFile().length() == 0)
            return;
        log.info("Searching for missing strings in " + languageFile.getName());
        UpdateReport report = YamlUpdater.create(languageFile, defaultLanguage.getLanguageFile())
                .backup(false)
                .update();
        if (report.isConfigChanged())
            log.info("Merged missing strings to " + languageFile.getName());
        else
            log.info(languageFile.getName() + " is up to date");
    }

    @Override
    public String getLanguageCode() {
        return languageCode;
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
    public boolean contains(String path) {
        return strings.contains(path);
    }

}
