package me.wyne.wutils.config.configurables.interaction;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

public interface InteractionAudienceAttribute {
    Audience get(CommandSender sender);
}
