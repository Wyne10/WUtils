package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.validation.EmptyValidator;
import me.wyne.wutils.i18n.language.validation.StringValidator;

public enum StringInterpreters {
    BASE(new BaseInterpreter(new EmptyValidator())),
    LEGACY(new LegacyInterpreter(new EmptyValidator()));

    private final StringInterpreter interpreter;

    StringInterpreters(StringInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public StringInterpreter getInterpreter() {
        return interpreter;
    }

    public void setStringValidator(StringValidator validator) {
        this.interpreter.setStringValidator(validator);
    }

    public StringInterpreter getInterpreter(StringValidator validator) {
        setStringValidator(validator);
        return this.interpreter;
    }
}
