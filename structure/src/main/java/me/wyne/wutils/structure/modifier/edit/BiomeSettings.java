package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import me.wyne.wutils.common.Args;
import org.jetbrains.annotations.NotNull;

/**
 * Config value for {@link BiomeEditModifier}, parsed by {@link #parse} from a single
 * space-delimited string: {@code margin(0) biomeId}. {@code biomeId} is required; an id without
 * a namespace is prefixed {@code minecraft:}, and an unknown id throws
 * {@link IllegalArgumentException}.
 */
public record BiomeSettings(int margin, @NotNull BiomeType biome) {

    public static @NotNull BiomeSettings parse(@NotNull String input) {
        var args = new Args(input, Args.SPACE_DELIMITER);
        int margin = Integer.parseInt(args.get(0, "0"));
        String id = args.get(1);
        BiomeType biome = BiomeTypes.get(id.contains(":") ? id : "minecraft:" + id);
        if (biome == null)
            throw new IllegalArgumentException("Unknown biome '" + id + "'");
        return new BiomeSettings(margin, biome);
    }

    @Override
    public @NotNull String toString() {
        return margin + " " + biome.getId();
    }
}
