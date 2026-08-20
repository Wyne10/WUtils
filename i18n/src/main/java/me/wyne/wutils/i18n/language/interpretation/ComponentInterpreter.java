package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Resolves localized {@link Component}s for a {@link Language}, optionally expanding PlaceholderAPI
 * placeholders and applying {@link TextReplacement}s, and converts between a {@code Component} and
 * this interpreter's own text format via {@link #toString} / {@link #fromString}.
 *
 * <p>Methods form the same grid as {@link StringInterpreter}: single value vs. {@code List}, plain vs.
 * placeholder-expanded (for a {@code Player} or an {@link OfflinePlayer}), and with or without
 * {@link TextReplacement}s. Implementations pair a serializer — legacy, enhanced legacy, or
 * MiniMessage — with either a plain flavor or an "Item" flavor that strips vanilla item-lore
 * italicization; see {@link LegacyInterpreter}, {@link EnhancedLegacyInterpreter} and
 * {@link MiniMessageInterpreter} alongside their {@code Item*} counterparts.</p>
 */
public interface ComponentInterpreter extends Interpreter {

    /**
     * Deserializes the raw string at {@code path} into a {@link Component}.
     */
    @NotNull Component getComponent(@NotNull Language language, @NotNull String path);

    //Component getComponent(ILanguage language, String path, TagResolver... tagResolvers);

    /**
     * @see #getComponent(Language, String)
     */
    @NotNull Component getComponent(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * Returns {@link #getComponent(Language, String)} with PlaceholderAPI placeholders expanded for
     * {@code player}; {@code null} still expands global (non-player) placeholders.
     */
    @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path);

    //Component getPlaceholderComponent(ILanguage language, @Nullable Player player, String path, TagResolver... tagResolvers);

    /**
     * @see #getPlaceholderComponent(Language, Player, String)
     */
    @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * @see #getPlaceholderComponent(Language, Player, String)
     */
    @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path);

    //Component getPlaceholderComponent(ILanguage language, @Nullable OfflinePlayer player, String path, TagResolver... tagResolvers);

    /**
     * @see #getPlaceholderComponent(Language, Player, String)
     */
    @NotNull Component getPlaceholderComponent(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * Deserializes each raw string in the list at {@code path} into a {@link Component}.
     */
    @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path);

    //List<Component> getComponentList(ILanguage language, String path, TagResolver... tagResolvers);

    /**
     * @see #getComponentList(Language, String)
     */
    @NotNull List<@NotNull Component> getComponentList(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * @see #getComponentList(Language, String)
     * @see #getPlaceholderComponent(Language, Player, String)
     */
    @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path);

    //List<Component> getPlaceholderComponentList(ILanguage language, @Nullable Player player, String path, TagResolver... tagResolvers);

    /**
     * @see #getPlaceholderComponentList(Language, Player, String)
     */
    @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * @see #getPlaceholderComponentList(Language, Player, String)
     */
    @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path);

    //List<Component> getPlaceholderComponentList(ILanguage language, @Nullable OfflinePlayer player, String path, TagResolver... tagResolvers);

    /**
     * @see #getPlaceholderComponentList(Language, Player, String)
     */
    @NotNull List<@NotNull Component> getPlaceholderComponentList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * Serializes {@code component} into this interpreter's text format (the inverse of {@link #fromString}).
     */
    @NotNull String toString(@NotNull Component component);

    /**
     * Deserializes {@code string} using this interpreter's text format (the inverse of {@link #toString}).
     */
    @NotNull Component fromString(@NotNull String string);
}
