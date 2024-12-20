package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.validation.EmptyValidator;
import me.wyne.wutils.i18n.language.validation.StringValidator;

public enum StringInterpreters {
    BASE(new BaseInterpreter(new EmptyValidator())),
    LEGACY(new EnhancedLegacyInterpreter(new EmptyValidator())),
    ENHANCED_LEGACY(new EnhancedLegacyInterpreter(new EmptyValidator())),
    MINI_MESSAGE(new MiniMessageInterpreter(new EmptyValidator()));

    private final StringInterpreter interpreter;

    StringInterpreters(StringInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public StringInterpreter get() {
        return interpreter;
    }

    public void setStringValidator(StringValidator validator) {
        this.interpreter.setStringValidator(validator);
    }

    public StringInterpreter get(StringValidator validator) {
        setStringValidator(validator);
        return this.interpreter;
    }
}
