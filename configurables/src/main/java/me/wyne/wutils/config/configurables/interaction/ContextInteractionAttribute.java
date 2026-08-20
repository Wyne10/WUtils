package me.wyne.wutils.config.configurables.interaction;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Delivers the "what" half of an interaction section to a resolved {@link Audience}.
 *
 * <p>{@code command} and {@code console} implementations are not audience-aware: they dispatch a
 * command once per configured list entry, ignoring the audience entirely, and always act as the
 * single {@code sender} handed to {@link #send(Audience, CommandSender, InteractionAttributeContext)} —
 * never once per audience member.</p>
 */
public interface ContextInteractionAttribute {
    default void send(@NotNull Audience audience, @NotNull CommandSender sender) {
        send(audience, sender, InteractionAttributeContext.EMPTY);
    }

    /**
     * Sends this attribute's payload to the given audience.
     *
     * @param sender the original sender the interaction was sent to, used by attributes that act as
     *               the sender rather than the audience (e.g. dispatched commands)
     * @param context text/component replacements and the placeholder target
     */
    void send(@NotNull Audience audience, @NotNull CommandSender sender, @NotNull InteractionAttributeContext context);
}
