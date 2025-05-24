package me.wyne.wutils.config.configurable;

import me.wyne.wutils.config.ConfigEntry;

public interface Configurable {

    /**
     * @see ConfigBuilder
     */
    String toConfig(ConfigEntry configEntry);

    void fromConfig(Object configObject);

}
