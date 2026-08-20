package me.wyne.wutils.config.configurables.animation;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Per-tick context an animation step's effect attributes build their {@link me.wyne.wutils.animation.AnimationRunnable}
 * from: an optional player, an optional location, and the text/component replacements.
 *
 * <p>Both {@code player} and {@code location} are commonly absent. Every {@link ContextAnimationAttribute}
 * checks what it needs and returns {@link me.wyne.wutils.animation.AnimationRunnable#EMPTY} rather than
 * failing when the context is missing it — see {@link ContextAnimationAttribute} for the full contract.
 * That makes a partially-supplied context safe, but also makes a mis-set-up animation look like a
 * config problem when it is a code problem.</p>
 */
public class AnimationContext {

    public static final AnimationContext EMPTY = new AnimationContext();

    @Nullable
    protected final Player player;
    @Nullable
    protected final Location location;
    protected final @NotNull TextReplacement[] textReplacements;
    protected final @NotNull ComponentReplacement[] componentReplacements;

    public AnimationContext(@Nullable Player player, @Nullable Location location, @NotNull TextReplacement[] textReplacements, @NotNull ComponentReplacement[] componentReplacements) {
        this.player = player;
        this.location = location;
        this.textReplacements = textReplacements;
        this.componentReplacements = componentReplacements;
    }

    public AnimationContext() {
        this(null, null, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public AnimationContext(@Nullable Player player) {
        this(player, null, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public AnimationContext(@Nullable Location location) {
        this(null, location, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public AnimationContext(@Nullable Player player, @Nullable Location location) {
        this(player, location, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public AnimationContext(@NotNull TextReplacement[] textReplacements, @NotNull ComponentReplacement[] componentReplacements) {
        this(null, null, textReplacements, componentReplacements);
    }

    public AnimationContext(@NotNull TextReplacement... textReplacements) {
        this(null, null, textReplacements, new ComponentReplacement[0]);
    }

    public AnimationContext(@NotNull ComponentReplacement... componentReplacements) {
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

    public @NotNull TextReplacement[] getTextReplacements() {
        return textReplacements;
    }

    public @NotNull ComponentReplacement[] getComponentReplacements() {
        return componentReplacements;
    }

}
