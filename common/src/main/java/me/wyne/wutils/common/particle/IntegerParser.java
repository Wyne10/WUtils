package me.wyne.wutils.common.particle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/** Parses an {@link Integer} from its decimal string form. */
public class IntegerParser implements StringDataParser<Integer> {

    private final static Collection<String> suggestions = new ArrayList<>() { {add("<int>");} };

    @Override
    public @NotNull Collection<@NotNull String> getSuggestions() {
        return suggestions;
    }

    /** @throws NumberFormatException if {@code string} is not a valid integer */
    @Override
    public @NotNull Integer getData(@NotNull String string) {
        return Integer.valueOf(string);
    }

    @Override
    public @NotNull String toString(@Nullable Object data) {
        return String.valueOf((int)data);
    }

}
