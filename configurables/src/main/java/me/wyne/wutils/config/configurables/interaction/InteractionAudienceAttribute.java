package me.wyne.wutils.config.configurables.interaction;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Resolves the "who" half of an interaction section into an {@link Audience}.
 *
 * <p>Every implementation resolves through {@code I18n.global.getAudiences()}, so the i18n module's
 * audience provider decides what "players" and "console" actually mean. Declared audiences are
 * unioned with {@link Audience#audience(Audience...)}, which does not de-duplicate — overlapping
 * audiences (e.g. {@code toAll} and {@code toPlayers} together) deliver twice.</p>
 */
public interface InteractionAudienceAttribute {
    /**
     * Resolves this attribute's audience.
     *
     * @param sender the sender the interaction was sent to, used by attributes whose audience is the
     *               sender itself
     */
    @NotNull Audience get(@NotNull CommandSender sender);
}
