package me.wyne.wutils.config.configurable;

import me.wyne.wutils.config.ConfigEntry;

/**
 * Specifies configurable which can be used by other configurables.
 */
public interface CompositeConfigurable extends Configurable {

    @Override
    default String toConfig(ConfigEntry configEntry) {
        return toConfig(ConfigBuilder.DEFAULT_DEPTH, configEntry);
    }
    String toConfig(int depth, ConfigEntry configEntry);

}
