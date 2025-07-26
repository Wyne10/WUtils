package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record ItemAttributeContext(Player player, TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {

    public static final ItemAttributeContext EMPTY = new ItemAttributeContext();

    public ItemAttributeContext() {
        this(null, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public ItemAttributeContext(Player player) {
        this(player, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public ItemAttributeContext(TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {
        this(null, textReplacements, componentReplacements);
    }

    public ItemAttributeContext(TextReplacement... textReplacements) {
        this(null, textReplacements, new ComponentReplacement[0]);
    }

    public ItemAttributeContext(ComponentReplacement... componentReplacements) {
        this(null, new TextReplacement[0], componentReplacements);
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public TextReplacement[] getTextReplacements() {
        return textReplacements;
    }

    public ComponentReplacement[] getComponentReplacements() {
        return componentReplacements;
    }

}
