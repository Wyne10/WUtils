package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.i18n.I18n;
import org.jetbrains.annotations.NotNull;

/** A {@link Replacement} over raw interpreter-formatted strings, as applied by {@link I18n#applyTextReplacements}. */
public interface TextReplacement extends Replacement<String> {

    /**
     * Adapts this replacement to operate on a {@link net.kyori.adventure.text.Component} by round-tripping
     * it through {@link I18n#global}'s component interpreter.
     *
     * @throws NullPointerException if {@link I18n#global} was never assigned
     */
    default @NotNull ComponentReplacement asComponentReplacement() {
        return component -> I18n.global.component().fromString(replace(I18n.global.component().toString(component)));
    }

    /** Returns a replacement that applies this one, then {@code replacement}, in order. */
    default @NotNull TextReplacement then(@NotNull TextReplacement replacement) {
        return obj -> {
            obj = this.replace(obj);
            obj = replacement.replace(obj);
            return obj;
        };
    }
}
