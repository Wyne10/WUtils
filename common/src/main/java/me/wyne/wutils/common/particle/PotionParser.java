package me.wyne.wutils.common.particle;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

/** Parses a legacy {@link Potion} by its {@link PotionType} exact enum constant name. */
@SuppressWarnings("deprecation")
public class PotionParser implements StringDataParser<Potion> {

    private final static Collection<String> suggestions = Arrays.stream(PotionType.values()).map(Enum::toString).toList();

    @Override
    public @NotNull Collection<@NotNull String> getSuggestions() {
        return suggestions;
    }

    /** @throws IllegalArgumentException if {@code string} is not a valid {@link PotionType} constant name */
    @Override
    public @NotNull Potion getData(@NotNull String string) {
        return new Potion(PotionType.valueOf(string));
    }

    @Override
    public @NotNull String toString(@Nullable Object data) {
        return ((Potion)data).getType().name();
    }

}
