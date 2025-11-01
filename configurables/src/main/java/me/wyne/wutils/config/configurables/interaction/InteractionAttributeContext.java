package me.wyne.wutils.config.configurables.interaction;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public record InteractionAttributeContext(@Nullable OfflinePlayer placeholderTarget, TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {

    public static final InteractionAttributeContext EMPTY = new InteractionAttributeContext();

    public InteractionAttributeContext() {
        this(null, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public InteractionAttributeContext(@Nullable OfflinePlayer placeholderTarget) {
        this(placeholderTarget, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public InteractionAttributeContext(TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {
        this(null, textReplacements, componentReplacements);
    }

    public InteractionAttributeContext(TextReplacement... textReplacements) {
        this(null, textReplacements, new ComponentReplacement[0]);
    }

    public InteractionAttributeContext(ComponentReplacement... componentReplacements) {
        this(null, new TextReplacement[0], componentReplacements);
    }

    @Nullable
    public OfflinePlayer getPlaceholderTarget() {
        return placeholderTarget;
    }

    public TextReplacement[] getTextReplacements() {
        return textReplacements;
    }

    public ComponentReplacement[] getComponentReplacements() {
        return componentReplacements;
    }

}
