package me.wyne.wutils.common.particle;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

/** Parses default {@link BlockData} by block material name, case-insensitively (via {@link Material#matchMaterial}). */
public class BlockDataParser implements StringDataParser<BlockData> {

    private final static Collection<String> suggestions = Arrays.stream(Material.values())
            .filter(Material::isBlock)
            .map(Enum::toString).toList();

    @Override
    public @NotNull Collection<@NotNull String> getSuggestions() {
        return suggestions;
    }

    /** @throws NullPointerException if {@code string} does not match any {@link Material} */
    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull BlockData getData(@NotNull String string) {
        return Material.matchMaterial(string).createBlockData();
    }

    @Override
    public @NotNull String toString(@Nullable Object data) {
        return ((BlockData)data).getMaterial().name();
    }

}
