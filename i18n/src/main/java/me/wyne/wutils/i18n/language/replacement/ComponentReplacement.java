package me.wyne.wutils.i18n.language.replacement;

import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/** A {@link Replacement} over Adventure {@link Component}s, as applied by {@link I18n#applyComponentReplacements}. */
public interface ComponentReplacement extends Replacement<Component> {

    /**
     * Adapts this replacement to operate on a raw interpreter-formatted string by round-tripping it
     * through {@link I18n#global}'s component interpreter.
     *
     * @throws NullPointerException if {@link I18n#global} was never assigned
     */
    default @NotNull TextReplacement asTextReplacement() {
        return string -> I18n.global.component().toString(replace(I18n.global.component().fromString(string)));
    }

    /** Returns a replacement that applies this one, then {@code replacement}, in order. */
    default @NotNull ComponentReplacement then(@NotNull ComponentReplacement replacement) {
        return obj -> {
            obj = this.replace(obj);
            obj = replacement.replace(obj);
            return obj;
        };
    }
}
