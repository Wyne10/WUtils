package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.Interpreter;
import org.jetbrains.annotations.NotNull;

/**
 * Base {@link Localized} implementation storing the interpreter, language and path a value was
 * resolved from.
 *
 * <p>The interpreter is stored as the raw {@link Interpreter} type and cast unchecked in
 * {@link #getInterpreter()}. Constructing an instance with an interpreter that does not actually
 * match {@code I} does not fail here — it fails later, as a {@link ClassCastException} at whatever
 * call site invokes {@link #getInterpreter()}.</p>
 */
public abstract class BaseLocalized<T, I extends Interpreter> implements Localized<T, I> {

    private final Interpreter interpreter;
    private final Language language;
    private final String path;

    public BaseLocalized(@NotNull Interpreter interpreter, @NotNull Language language, @NotNull String path) {
        this.interpreter = interpreter;
        this.language = language;
        this.path = path;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull I getInterpreter() {
        return (I) interpreter;
    }

    @Override
    public @NotNull Language getLanguage() {
        return language;
    }

    @Override
    public @NotNull String getPath() {
        return path;
    }

}
