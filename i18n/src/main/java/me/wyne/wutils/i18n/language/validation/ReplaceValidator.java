package me.wyne.wutils.i18n.language.validation;

import java.util.Map;

public class ReplaceValidator implements StringValidator {

    private final String replaceString;

    public ReplaceValidator(String replaceString)
    {
        this.replaceString = replaceString;
    }

    @Override
    public String validateString(Map<String, String> strings, String path) {
        return strings.getOrDefault(path, replaceString);
    }

}
