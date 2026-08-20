package me.wyne.wutils.i18n.language.validation;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/** Throws {@link IllegalArgumentException} instead of falling back when the path is missing. */
public class ExceptionValidator implements StringValidator {
    @Override
    public @NotNull String validateString(@NotNull Map<@NotNull String, @NotNull String> strings, @NotNull String path) {
        if (!strings.containsKey(path))
            throw new IllegalArgumentException("String " + path + " was not found");
        return strings.get(path);
    }
}
