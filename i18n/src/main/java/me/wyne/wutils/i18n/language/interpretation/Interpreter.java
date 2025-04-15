package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.validation.StringValidator;

public interface Interpreter {
    void setStringValidator(StringValidator stringValidator);
    StringValidator getStringValidator();
}
