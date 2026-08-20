package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.StringInterpreter;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.jetbrains.annotations.NotNull;

/**
 * A localized string resolved via a {@link StringInterpreter}.
 */
public class LocalizedString extends BaseLocalized<String, StringInterpreter> {

    private final String string;

    public LocalizedString(@NotNull StringInterpreter interpreter, @NotNull Language language, @NotNull String path, @NotNull String string) {
        super(interpreter, language, path);
        this.string = string;
    }

    /**
     * Returns a new {@code LocalizedString} with every replacement applied to this string.
     */
    public @NotNull LocalizedString replace(@NotNull TextReplacement... textReplacements) {
        return new LocalizedString(getInterpreter(), getLanguage(), getPath(), I18n.applyTextReplacements(string, textReplacements));
    }

    @Override
    public @NotNull String get() {
        return string;
    }

    @Override
    public @NotNull String toString() {
        return string;
    }

}
