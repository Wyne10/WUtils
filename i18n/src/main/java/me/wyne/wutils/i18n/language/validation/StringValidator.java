package me.wyne.wutils.i18n.language.validation;

import java.util.Map;

@FunctionalInterface
public interface StringValidator {

    String validateString(Map<String, String> strings, String path);

}
