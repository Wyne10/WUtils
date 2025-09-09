package me.wyne.wutils.i18n.language.validation;

import java.util.Map;

public class EmptyValidator implements StringValidator {
    @Override
    public String validateString(Map<String, String> strings, String path) {
        return strings.getOrDefault(path, path);
    }
}
