package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ItemAttributeContext {

    public static final ItemAttributeContext EMPTY = new ItemAttributeContext();

    private Player player;
    private TextReplacement[] textReplacements = {};
    private ComponentReplacement[] componentReplacements = {};

    public ItemAttributeContext() {}

    public ItemAttributeContext(Player player, TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {
        this.player = player;
        this.textReplacements = textReplacements;
        this.componentReplacements = componentReplacements;
    }

    public ItemAttributeContext(Player player) {
        this.player = player;
    }

    public ItemAttributeContext(TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {
        this.textReplacements = textReplacements;
        this.componentReplacements = componentReplacements;
    }

    public ItemAttributeContext(TextReplacement... textReplacements) {
        this.textReplacements = textReplacements;
    }

    public ItemAttributeContext(ComponentReplacement... componentReplacements) {
        this.componentReplacements = componentReplacements;
    }

    public ItemAttributeContext setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ItemAttributeContext setTextReplacements(TextReplacement[] textReplacements) {
        this.textReplacements = textReplacements;
        return this;
    }

    public ItemAttributeContext setComponentReplacements(ComponentReplacement[] componentReplacements) {
        this.componentReplacements = componentReplacements;
        return this;
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
