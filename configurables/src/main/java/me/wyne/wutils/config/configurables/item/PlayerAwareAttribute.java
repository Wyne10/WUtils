package me.wyne.wutils.config.configurables.item;

import org.bukkit.entity.Player;

public interface PlayerAwareAttribute {
    void apply(Player player);
}
