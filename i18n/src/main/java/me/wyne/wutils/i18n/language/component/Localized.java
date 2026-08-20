package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.Interpreter;
import org.jetbrains.annotations.NotNull;

/**
 * A single localized value ({@code T}) resolved from a {@link Language} at a given path, together
 * with the {@link Interpreter} ({@code I}) that produced it.
 */
public interface Localized<T, I extends Interpreter> {

    /**
     * Returns the interpreter that resolved this value.
     */
    @NotNull I getInterpreter();

    /**
     * Returns the language this value was resolved from.
     */
    @NotNull Language getLanguage();

    /**
     * Returns the path this value was resolved at.
     */
    @NotNull String getPath();

    /**
     * Returns the resolved value.
     */
    @NotNull T get();

}
