package me.wyne.wutils.config.configurables.interaction;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Per-send context for a {@link ContextInteractionAttribute}: the placeholder target and the
 * text/component replacements substituted into the interaction's text.
 *
 * <p>{@code placeholderTarget} is deliberately independent of the {@code sender} passed to
 * {@link ContextInteractionAttribute#send}: the sender decides the interaction's language via
 * {@code I18n.global.accessor(sender, ...)}, while the placeholder target decides whose
 * PlaceholderAPI placeholders are filled in. They are usually the same player.</p>
 */
public record InteractionAttributeContext(@Nullable OfflinePlayer placeholderTarget, @NotNull TextReplacement[] textReplacements, @NotNull ComponentReplacement[] componentReplacements) {

    public static final InteractionAttributeContext EMPTY = new InteractionAttributeContext();

    public InteractionAttributeContext() {
        this(null, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public InteractionAttributeContext(@Nullable OfflinePlayer placeholderTarget) {
        this(placeholderTarget, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public InteractionAttributeContext(@NotNull TextReplacement[] textReplacements, @NotNull ComponentReplacement[] componentReplacements) {
        this(null, textReplacements, componentReplacements);
    }

    public InteractionAttributeContext(@NotNull TextReplacement... textReplacements) {
        this(null, textReplacements, new ComponentReplacement[0]);
    }

    public InteractionAttributeContext(@NotNull ComponentReplacement... componentReplacements) {
        this(null, new TextReplacement[0], componentReplacements);
    }

    @Nullable
    public OfflinePlayer getPlaceholderTarget() {
        return placeholderTarget;
    }

    public @NotNull TextReplacement[] getTextReplacements() {
        return textReplacements;
    }

    public @NotNull ComponentReplacement[] getComponentReplacements() {
        return componentReplacements;
    }

}
