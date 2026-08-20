package me.wyne.wutils.i18n.language;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * {@link LanguageStrings} view over a flat {@code .lang} key/value map. The format has no list concept:
 * {@link #isList} always returns {@code false}, and {@link #getStringList} returns a single-element list
 * for a present key rather than resolving an actual list.
 */
public record LangLanguageStrings(@NotNull Map<@NotNull String, @NotNull String> values) implements LanguageStrings {

    @Override
    public boolean contains(@NotNull String path) {
        return values.containsKey(path);
    }

    @Override
    public boolean isList(@NotNull String path) {
        return false;
    }

    @Override
    public @NotNull List<@NotNull String> getStringList(@NotNull String path) {
        List<String> result = new LinkedList<>();
        String value = values.get(path);
        if (value != null)
            result.add(value);
        return result;
    }

}
