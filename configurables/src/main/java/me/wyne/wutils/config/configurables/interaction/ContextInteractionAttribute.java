package me.wyne.wutils.config.configurables.interaction;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

public interface ContextInteractionAttribute {
    default void send(Audience audience, CommandSender sender) {
        send(audience, sender, InteractionAttributeContext.EMPTY);
    }

    void send(Audience audience, CommandSender sender, InteractionAttributeContext context);
}
