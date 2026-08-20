package me.wyne.wutils.config.configurable;

import me.wyne.wutils.config.ConfigEntry;
import org.jetbrains.annotations.NotNull;

/**
 * Specifies a configurable that can be nested inside another configurable's generated YAML, rather
 * than only serialized on its own.
 */
public interface CompositeConfigSerializable extends ConfigSerializable {

    /**
     * Renders this object's state at the given indentation depth, so it can be embedded as a
     * sub-section of an enclosing config (see {@link ConfigBuilder#appendComposite}).
     *
     * @param depth the nesting depth, in {@link ConfigBuilder} indentation levels, to render at
     */
    @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry);

    @Override
    default @NotNull String toConfig(@NotNull ConfigEntry configEntry) {
        return toConfig(ConfigBuilder.DEFAULT_DEPTH, configEntry);
    }

}
