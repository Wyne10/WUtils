package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Color}, configurable either as a {@code red}/{@code green}/{@code blue} section or as a
 * {@code #rrggbb} hex string, and rendered back into generated config as its packed RGB integer.
 */
public class ColorAttribute extends ConfigurableAttribute<Color> {

    public ColorAttribute(@NotNull String key, @NotNull Color value) {
        super(key, value);
    }

    /**
     * Writes {@code '#RRGGBB'}. The decimal {@code asRGB()} form this used to write reloads as a YAML
     * integer, which {@link Factory} sees as neither a string nor a section — it would fall through to
     * {@link Factory#fromSection} and silently yield black.
     */
    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), toHex(getValue())).buildNoSpace();
    }

    /** Renders {@code color} as {@code #RRGGBB}, the form {@link Factory#fromString} parses. */
    public static @NotNull String toHex(@NotNull Color color) {
        return String.format("#%06X", color.asRGB());
    }

    public static final class Factory implements CompositeAttributeFactory<ColorAttribute> {
        @Override
        public @NotNull ColorAttribute fromSection(@NotNull String key, @NotNull ConfigurationSection section) {
            return new ColorAttribute(
                    key,
                    Color.fromRGB(
                            section.getInt("red", 0),
                            section.getInt("green", 0),
                            section.getInt("blue", 0)
                    )
            );
        }

        @Override
        public @NotNull ColorAttribute fromString(@NotNull String key, @NotNull String string, @NotNull ConfigurationSection config) {
            return new ColorAttribute(
                    key,
                    Color.fromRGB(Integer.parseUnsignedInt(string.replace("#", ""), 16))
            );
        }
    }

}
