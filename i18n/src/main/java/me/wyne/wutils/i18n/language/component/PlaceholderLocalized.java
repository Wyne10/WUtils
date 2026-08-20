package me.wyne.wutils.i18n.language.component;

import me.wyne.wutils.i18n.language.interpretation.Interpreter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Localized} value that was resolved with PlaceholderAPI placeholders expanded for a
 * specific (possibly offline, possibly absent) player.
 */
public interface PlaceholderLocalized<T, I extends Interpreter> extends Localized<T, I> {

    /**
     * Returns the player placeholders were expanded for, or {@code null} if none was given.
     */
    @Nullable
    OfflinePlayer getOfflinePlayer();

    /**
     * Returns {@link #getOfflinePlayer()} as an online {@link Player}, or {@code null} if the player
     * is absent or offline.
     */
    @Nullable
    Player getPlayer();

}
