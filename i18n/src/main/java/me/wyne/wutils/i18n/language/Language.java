package me.wyne.wutils.i18n.language;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Locale;

public interface Language {
    String getLanguageCode();

    Locale getLocale();

    File getLanguageFile();

    FileConfiguration getStrings();

    boolean contains(String path);
}
