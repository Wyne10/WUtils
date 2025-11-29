package me.wyne.wutils.common.particle;

import org.bukkit.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

public class ColorParser implements StringDataParser<Color> {

    private final static Collection<String> suggestions = Arrays.stream(Color.class.getDeclaredFields())
            .filter(field -> Modifier.isStatic(field.getModifiers()))
            .map(Field::getName)
            .filter(name -> !name.equals("BIT_MASK")).toList();

    @Override
    public Collection<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public Color getData(String string) {
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
    public String toString(Object data) {
        return String.valueOf(((Color)data).asRGB());
    }

}
