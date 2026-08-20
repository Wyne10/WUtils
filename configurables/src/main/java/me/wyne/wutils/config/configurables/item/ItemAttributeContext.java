package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Per-build context for an item attribute: the player and the text/component replacements
 * substituted into resolved i18n strings, chiefly for {@code name} and {@code lore}.
 *
 * <p>A {@code null} player means no player-specific language or PlaceholderAPI source is used.</p>
 */
public record ItemAttributeContext(@Nullable Player player, @NotNull TextReplacement[] textReplacements, @NotNull ComponentReplacement[] componentReplacements) {

    public static final ItemAttributeContext EMPTY = new ItemAttributeContext();

    public ItemAttributeContext() {
        this(null, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public ItemAttributeContext(@Nullable Player player) {
        this(player, new TextReplacement[0], new ComponentReplacement[0]);
    }

    public ItemAttributeContext(@NotNull TextReplacement[] textReplacements, @NotNull ComponentReplacement[] componentReplacements) {
        this(null, textReplacements, componentReplacements);
    }

    public ItemAttributeContext(@NotNull TextReplacement... textReplacements) {
        this(null, textReplacements, new ComponentReplacement[0]);
    }

    public ItemAttributeContext(@NotNull ComponentReplacement... componentReplacements) {
        this(null, new TextReplacement[0], componentReplacements);
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public @NotNull TextReplacement[] getTextReplacements() {
        return textReplacements;
    }

    public @NotNull ComponentReplacement[] getComponentReplacements() {
        return componentReplacements;
    }

}
