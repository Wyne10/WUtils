package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;

public interface CompositeConfigurable {
    String toConfig(int depth, ConfigEntry configEntry);
}
