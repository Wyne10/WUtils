package me.wyne.wutils.config.configurable;

import me.wyne.wutils.config.ConfigEntry;
import org.jetbrains.annotations.NotNull;

/**
 * Specifies configurable which can be serialized to config.
 */
public interface ConfigSerializable {

    /**
     * Renders this object's state as generated YAML text, typically built with a {@link ConfigBuilder}.
     */
    @NotNull String toConfig(@NotNull ConfigEntry configEntry);

}
