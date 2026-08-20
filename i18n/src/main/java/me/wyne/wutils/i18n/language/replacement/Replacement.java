package me.wyne.wutils.i18n.language.replacement;

import org.jetbrains.annotations.NotNull;

/** A transformation from a value to a replacement of the same type. Base of {@link TextReplacement} and {@link ComponentReplacement}. */
@FunctionalInterface
public interface Replacement<T> {

    @NotNull T replace(@NotNull T obj);

}
