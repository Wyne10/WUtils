package me.wyne.wutils.i18n.language;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Locale;
import java.util.Map;

/**
 * A single loaded language backed by a source file (YAML, JSON or {@code .lang}), exposing its raw
 * strings both as a flat lookup map and as a {@link LanguageStrings} view; both resolve nested paths.
 */
public interface Language {
    @NotNull String getLanguageCode();

    @NotNull Locale getLocale();

    @NotNull File getLanguageFile();

    @NotNull LanguageStrings getStrings();

    /**
     * Returns a flat map of every string in the language file, keyed by its full dotted path.
     *
     * <p>Nesting is flattened, so a value at {@code messages.welcome} is present under exactly that key
     * regardless of the backing format. Single-string lookups (e.g. by a
     * {@link me.wyne.wutils.i18n.language.validation.StringValidator}) go through this map; lists are
     * reached through {@link #getStrings()} instead.</p>
     */
    @NotNull Map<@NotNull String, @NotNull String> getStringMap();
}
