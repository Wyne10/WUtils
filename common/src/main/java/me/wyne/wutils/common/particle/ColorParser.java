package me.wyne.wutils.common.particle;

import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

/**
 * Parses a {@link Color} either by the name of one of {@link Color}'s predefined static fields
 * (e.g. {@code "RED"}) or, for anything else, as a 24-bit RGB hex value (e.g. {@code "#FF0000"}
 * or {@code "FF0000"}, {@code #} optional).
 */
public class ColorParser implements StringDataParser<Color> {

    private final static Collection<String> suggestions = Arrays.stream(Color.class.getDeclaredFields())
            .filter(field -> Modifier.isStatic(field.getModifiers()))
            .map(Field::getName)
            .filter(name -> !name.equals("BIT_MASK")).toList();

    @Override
    public @NotNull Collection<@NotNull String> getSuggestions() {
        return suggestions;
    }

    /** @throws NumberFormatException if {@code string} is neither a known color name nor valid hex */
    @Override
    public @NotNull Color getData(@NotNull String string) {
        if (suggestions.contains(string)) {
            try {
                return (Color) Color.class.getField(string).get(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                return Color.fromRGB(Integer.parseUnsignedInt(string.replace("#", ""), 16));
            }
        }
        else
            return Color.fromRGB(Integer.parseUnsignedInt(string.replace("#", ""), 16));
    }

    @Override
    public @NotNull String toString(@Nullable Object data) {
        return String.valueOf(((Color)data).asRGB());
    }

}
