package me.wyne.wutils.i18n;

import org.bukkit.configuration.file.FileConfiguration;

public interface StringValidator {

    String validateString(String languageCode, FileConfiguration strings, String path);

}
