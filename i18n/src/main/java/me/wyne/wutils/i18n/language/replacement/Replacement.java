package me.wyne.wutils.i18n.language.replacement;

@FunctionalInterface
public interface Replacement<T> {

    T replace(T obj);
    default Replacement<T> then(Replacement<T> replacement) {
        return obj -> {
            obj = this.replace(obj);
            obj = replacement.replace(obj);
            return obj;
        };
    }

}
