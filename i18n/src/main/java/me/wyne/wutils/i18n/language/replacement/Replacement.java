package me.wyne.wutils.i18n.language.replacement;

@FunctionalInterface
public interface Replacement<T> {

    T replace(T obj);

}
