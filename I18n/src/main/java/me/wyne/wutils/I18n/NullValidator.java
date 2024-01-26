package me.wyne.wutils.i18n;

import org.bukkit.configuration.file.FileConfiguration;

public class NullValidator implements StringValidator {
    @Override
    public String validateString(String languageCode, FileConfiguration strings, String path) {
        return strings.getString(path);
    }
}
