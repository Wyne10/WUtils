package me.wyne.wutils.i18n.language.validation;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/** Falls back to a fixed configured string when the path is missing. */
public class ReplaceValidator implements StringValidator {

    private final String replaceString;

    public ReplaceValidator(@NotNull String replaceString)
    {
        this.replaceString = replaceString;
    }

    @Override
    public @NotNull String validateString(@NotNull Map<@NotNull String, @NotNull String> strings, @NotNull String path) {
        return strings.getOrDefault(path, replaceString);
    }

}
