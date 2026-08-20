package me.wyne.wutils.common.particle;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

/** Parses a {@link BlockFace} by its exact enum constant name. */
public class BlockFaceParser implements StringDataParser<BlockFace> {

    private final static Collection<String> suggestions = Arrays.stream(BlockFace.values()).map(Enum::toString).toList();

    @Override
    public @NotNull Collection<@NotNull String> getSuggestions() {
        return suggestions;
    }

    /** @throws IllegalArgumentException if {@code string} is not a valid {@link BlockFace} constant name */
    @Override
    public @NotNull BlockFace getData(@NotNull String string) {
        return BlockFace.valueOf(string);
    }

    @Override
    public @NotNull String toString(@Nullable Object data) {
        return ((BlockFace)data).name();
    }

}
