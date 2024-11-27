package me.wyne.wutils.i18n.language;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public interface Language {
    String getLanguageCode();

    File getLanguageFile();

    FileConfiguration getStrings();

    boolean contains(String path);
}
