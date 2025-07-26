package me.wyne.wutils.config.configurables.animation;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class AnimationContext {

    public static final AnimationContext EMPTY = new AnimationContext();

    private Player player;
    private Location location;
    private TextReplacement[] textReplacements = {};
    private ComponentReplacement[] componentReplacements = {};

    public AnimationContext() {}

    public AnimationContext(Player player, Location location, TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {
        this.player = player;
        this.location = location;
        this.textReplacements = textReplacements;
        this.componentReplacements = componentReplacements;
    }

    public AnimationContext(Player player) {
        this.player = player;
    }

    public AnimationContext(Location location) {
        this.location = location;
    }

    public AnimationContext(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    public AnimationContext(TextReplacement[] textReplacements, ComponentReplacement[] componentReplacements) {
        this.textReplacements = textReplacements;
        this.componentReplacements = componentReplacements;
    }

    public AnimationContext(TextReplacement... textReplacements) {
        this.textReplacements = textReplacements;
    }

    public AnimationContext(ComponentReplacement... componentReplacements) {
        this.componentReplacements = componentReplacements;
    }

    public AnimationContext setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public AnimationContext setLocation(Location location) {
        this.location = location;
        return this;
    }

    public AnimationContext setTextReplacements(TextReplacement[] textReplacements) {
        this.textReplacements = textReplacements;
        return this;
    }

    public AnimationContext setComponentReplacements(ComponentReplacement[] componentReplacements) {
        this.componentReplacements = componentReplacements;
        return this;
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
