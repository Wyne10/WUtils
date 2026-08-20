package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.validation.StringValidator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Factory for the available {@link StringInterpreter} implementations.
 */
public enum StringInterpreters {
    BASE(BaseInterpreter::new);

    private final Function<StringValidator, StringInterpreter> factory;

    StringInterpreters(@NotNull Function<StringValidator, StringInterpreter> factory) {
        this.factory = factory;
    }

    /**
     * Creates a new interpreter of this kind, validating lookups with {@code validator}.
     */
    public @NotNull StringInterpreter get(@NotNull StringValidator validator) {
        return factory.apply(validator);
    }
}
