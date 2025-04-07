package me.wyne.wutils.common.loadable;

import org.bukkit.configuration.ConfigurationSection;

public interface Loadable {
    void load(ConfigurationSection config);
}
