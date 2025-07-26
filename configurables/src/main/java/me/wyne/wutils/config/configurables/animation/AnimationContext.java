package me.wyne.wutils.config.configurables.animation;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record AnimationContext(Player player, Location location, TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {

    public static final AnimationContext EMPTY = new AnimationContext();

    public AnimationContext() {
        this(null, null, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public AnimationContext(Player player) {
        this(player, null, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public AnimationContext(Location location) {
        this(null, location, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public AnimationContext(Player player, Location location) {
        this(player, location, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public AnimationContext(TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {
        this(null, null, textReplacements, componentReplacements);
    }

    public AnimationContext(TextReplacement... textReplacements) {
        this(null, null, textReplacements, new ComponentReplacement[0]);
    }

    public AnimationContext(ComponentReplacement... componentReplacements) {
        this(null, null, new TextReplacement[0], componentReplacements);
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    public TextReplacement[] getTextReplacements() {
        return textReplacements;
    }

    public ComponentReplacement[] getComponentReplacements() {
        return componentReplacements;
    }

}
