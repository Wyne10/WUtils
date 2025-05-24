package me.wyne.wutils.config.configurable;

import me.wyne.wutils.config.ConfigEntry;
import org.jetbrains.annotations.Nullable;

/**
 * Specifies configurable which can be used by other configurables.
 */
public interface CompositeConfigurable extends Configurable {

    String toConfig(int depth, ConfigEntry configEntry);

    @Override
    default String toConfig(ConfigEntry configEntry) {
        return toConfig(ConfigBuilder.DEFAULT_DEPTH, configEntry);
    }

    @Override
    void fromConfig(@Nullable Object configObject);

}
