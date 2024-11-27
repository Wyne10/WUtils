package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import me.wyne.wutils.i18n.language.validation.StringValidator;

public interface Interpreter {
    default String applyTextReplacements(String string, TextReplacement...textReplacements) {
        String result = string;
        for (TextReplacement replacement : textReplacements)
            result = replacement.replace(result);
        return result;
    }

    void setStringValidator(StringValidator stringValidator);
}
