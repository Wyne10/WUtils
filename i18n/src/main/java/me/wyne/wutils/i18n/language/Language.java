package me.wyne.wutils.i18n.language;

import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.Locale;
import java.util.Map;

public interface Language {
    String getLanguageCode();

    Locale getLocale();

    File getLanguageFile();

    ConfigurationSection getStrings();

    Map<String, String> getStringMap();

    boolean contains(String path);
}
