package me.wyne.wutils.i18n.language.validation;

import org.bukkit.configuration.file.FileConfiguration;

public class NullValidator implements StringValidator {
    @Override
    public String validateString(String languageCode, FileConfiguration strings, String path) {
        if (path.trim().isEmpty())
            return null;
        return strings.getString(path);
    }
}
