package me.wyne.wutils.i18n.language.validation;

import org.bukkit.configuration.file.FileConfiguration;

public class ReplaceValidator implements StringValidator {

    private final String replaceString;

    public ReplaceValidator(String replaceString)
    {
        this.replaceString = replaceString;
    }

    @Override
    public String validateString(String languageCode, FileConfiguration strings, String path) {
        return strings.contains(path) ? strings.getString(path) : replaceString;
    }
}
