package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.validation.StringValidator;
import org.jetbrains.annotations.NotNull;

/**
 * Base contract shared by {@link StringInterpreter} and {@link ComponentInterpreter}.
 *
 * <p>Implementations are mutable and shared: an {@code I18n} instance holds a single string
 * interpreter and a single component interpreter, so replacing the {@link StringValidator} via
 * {@link #setStringValidator} changes the validation behavior of every lookup made through that
 * instance, not just future ones created afterwards.</p>
 */
public interface Interpreter {

    /**
     * Replaces the validator used to resolve raw strings for every subsequent lookup.
     */
    void setStringValidator(@NotNull StringValidator stringValidator);

    /**
     * Returns the validator currently used to resolve raw strings.
     */
    @NotNull StringValidator getStringValidator();
}
