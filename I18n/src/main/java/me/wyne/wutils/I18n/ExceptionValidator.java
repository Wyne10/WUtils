package me.wyne.wutils.i18n;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.NoSuchElementException;

public class ExceptionValidator implements StringValidator {
    @Override
    public String validateString(String languageCode, FileConfiguration strings, String path) {
        throw new IllegalArgumentException("String " + path + " was not found in " + languageCode + " language");
    }
}
