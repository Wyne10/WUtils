package me.wyne.wutils.i18n.language.validation;

import org.bukkit.configuration.file.FileConfiguration;

public class ExceptionValidator implements StringValidator {
    @Override
    public String validateString(String languageCode, FileConfiguration strings, String path) {
        if (path.trim().isEmpty() || !strings.contains(path))
            throw new IllegalArgumentException("String " + path + " was not found in " + languageCode + " language");
        return strings.getString(path);
    }
}
