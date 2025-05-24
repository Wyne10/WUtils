package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.interpretation.Interpreter;

public interface Localized<T, I extends Interpreter> {

    I getInterpreter();

    Language getLanguage();

    String getPath();

    T get();

}
