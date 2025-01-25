package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.validation.EmptyValidator;
import me.wyne.wutils.i18n.language.validation.StringValidator;

public enum ComponentInterpreters {
    LEGACY(new LegacyInterpreter(new EmptyValidator())),
    ENHANCED_LEGACY(new EnhancedLegacyInterpreter(new EmptyValidator())),
    MINI_MESSAGE(new MiniMessageInterpreter(new EmptyValidator()));

    private final ComponentInterpreter interpreter;

    ComponentInterpreters(ComponentInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public ComponentInterpreter get() {
        return interpreter;
    }

    public void setStringValidator(StringValidator validator) {
        this.interpreter.setStringValidator(validator);
    }

    public ComponentInterpreter get(StringValidator validator) {
        setStringValidator(validator);
        return this.interpreter;
    }
}
