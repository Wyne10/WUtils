package me.wyne.wutils.config.configurable;

import me.wyne.wutils.config.ConfigEntry;

/**
 * Specifies configurable which can be serialized to config.
 */
public interface ConfigSerializable {

    /**
     * @see ConfigBuilder
     */
    String toConfig(ConfigEntry configEntry);

}
