package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.validation.StringValidator;

import java.util.function.Function;

public enum StringInterpreters {
    BASE(BaseInterpreter::new),
    LEGACY(LegacyInterpreter::new);

    private final Function<StringValidator, StringInterpreter> factory;

    StringInterpreters(Function<StringValidator, StringInterpreter> factory) {
        this.factory = factory;
    }

    public StringInterpreter get(StringValidator validator) {
        return factory.apply(validator);
    }
}
