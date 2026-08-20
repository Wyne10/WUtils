package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.Language;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Resolves localized strings for a {@link Language}, optionally expanding PlaceholderAPI placeholders
 * and applying {@link TextReplacement}s.
 *
 * <p>Methods form a regular grid: single value vs. {@code List}, plain vs. placeholder-expanded (for
 * a {@code Player} or an {@link OfflinePlayer}), and with or without {@link TextReplacement}s. See
 * {@link BaseInterpreter}, the sole implementation, for the lookup semantics behind {@code path}.</p>
 */
public interface StringInterpreter extends Interpreter {

    /**
     * Returns the raw string stored at {@code path} in {@code language}.
     */
    @NotNull String getString(@NotNull Language language, @NotNull String path);

    /**
     * @see #getString(Language, String)
     */
    @NotNull String getString(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * Returns {@link #getString(Language, String)} with PlaceholderAPI placeholders expanded for
     * {@code player}; {@code null} still expands global (non-player) placeholders.
     */
    @NotNull String getPlaceholderString(@NotNull Language language, @Nullable Player player, @NotNull String path);

    /**
     * @see #getPlaceholderString(Language, Player, String)
     */
    @NotNull String getPlaceholderString(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * @see #getPlaceholderString(Language, Player, String)
     */
    @NotNull String getPlaceholderString(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path);

    /**
     * @see #getPlaceholderString(Language, Player, String)
     */
    @NotNull String getPlaceholderString(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * Returns the raw strings stored at the list {@code path} in {@code language}.
     */
    @NotNull List<@NotNull String> getStringList(@NotNull Language language, @NotNull String path);

    /**
     * @see #getStringList(Language, String)
     */
    @NotNull List<@NotNull String> getStringList(@NotNull Language language, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * @see #getStringList(Language, String)
     * @see #getPlaceholderString(Language, Player, String)
     */
    @NotNull List<@NotNull String> getPlaceholderStringList(@NotNull Language language, @Nullable Player player, @NotNull String path);

    /**
     * @see #getPlaceholderStringList(Language, Player, String)
     */
    @NotNull List<@NotNull String> getPlaceholderStringList(@NotNull Language language, @Nullable Player player, @NotNull String path, @NotNull TextReplacement... textReplacements);

    /**
     * @see #getPlaceholderStringList(Language, Player, String)
     */
    @NotNull List<@NotNull String> getPlaceholderStringList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path);

    /**
     * @see #getPlaceholderStringList(Language, Player, String)
     */
    @NotNull List<@NotNull String> getPlaceholderStringList(@NotNull Language language, @Nullable OfflinePlayer player, @NotNull String path, @NotNull TextReplacement... textReplacements);
}
