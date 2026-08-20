package me.wyne.wutils.i18n.language.validation;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/** Falls back to the path itself when it is missing, so an unresolved key renders as its own name. */
public class EmptyValidator implements StringValidator {
    @Override
    public @NotNull String validateString(@NotNull Map<@NotNull String, @NotNull String> strings, @NotNull String path) {
        return strings.getOrDefault(path, path);
    }
}
