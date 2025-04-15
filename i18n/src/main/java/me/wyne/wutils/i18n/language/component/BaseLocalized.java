package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.Interpreter;

public abstract class BaseLocalized<T, I extends Interpreter> implements Localized<T, I> {

    private final Interpreter interpreter;
    private final Language language;
    private final String path;

    public BaseLocalized(Interpreter interpreter, Language language, String path) {
        this.interpreter = interpreter;
        this.language = language;
        this.path = path;
    }

    @SuppressWarnings("unchecked")
    @Override
    public I getInterpreter() {
        return (I) interpreter;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public String getPath() {
        return path;
    }

}
