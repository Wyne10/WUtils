package me.wyne.wutils.i18n.language.validation;

import org.bukkit.configuration.file.FileConfiguration;

@FunctionalInterface
public interface StringValidator {

    String validateString(String languageCode, FileConfiguration strings, String path);

}
