package me.wyne.wutils.i18n.language.validation;

import java.util.Map;

public class ExceptionValidator implements StringValidator {
    @Override
    public String validateString(String languageCode, Map<String, String> strings, String path) {
        if (!strings.containsKey(path))
            throw new IllegalArgumentException("String " + path + " was not found in " + languageCode + " language");
        return strings.get(path);
    }
}
