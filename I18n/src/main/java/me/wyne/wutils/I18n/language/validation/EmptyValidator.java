package me.wyne.wutils.i18n.language.validation;

import org.bukkit.configuration.file.FileConfiguration;

public class EmptyValidator implements StringValidator {
    @Override
    public String validateString(String languageCode, FileConfiguration strings, String path) {
        return strings.contains(path) ? strings.getString(path) : path;
    }
}
