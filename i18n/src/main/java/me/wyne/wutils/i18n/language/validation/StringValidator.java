package me.wyne.wutils.i18n.language.validation;

import java.util.Map;

@FunctionalInterface
public interface StringValidator {

    String validateString(String languageCode, Map<String, String> strings, String path);

}
