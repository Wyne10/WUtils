package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.StringInterpreter;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;

public class LocalizedString extends BaseLocalized<String, StringInterpreter> {

    private final String string;

    public LocalizedString(StringInterpreter interpreter, Language language, String path, String string) {
        super(interpreter, language, path);
        this.string = string;
    }

    public LocalizedString replace(TextReplacement... textReplacements) {
        return new LocalizedString(getInterpreter(), getLanguage(), getPath(), I18n.applyTextReplacements(string, textReplacements));
    }

    @Override
    public String get() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }

}
