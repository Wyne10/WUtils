package me.wyne.wutils.common.particle;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

/** Parses a {@link Material} by its enum name or legacy alias, case-insensitively (via {@link Material#matchMaterial}). */
public class MaterialParser implements StringDataParser<Material> {

    private final static Collection<String> suggestions = Arrays.stream(Material.values()).map(Enum::toString).toList();

    @Override
    public @NotNull Collection<@NotNull String> getSuggestions() {
        return suggestions;
    }

    /** Returns {@code null} if {@code string} does not match any {@link Material}, rather than throwing. */
    @Override
    public @Nullable Material getData(@NotNull String string) {
        return Material.matchMaterial(string);
    }

    @Override
    public @NotNull String toString(@Nullable Object data) {
        return ((Material)data).name();
    }

}
