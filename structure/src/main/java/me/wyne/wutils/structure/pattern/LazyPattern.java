package me.wyne.wutils.structure.pattern;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.Pattern;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public record LazyPattern(@NotNull String pattern) {

    public Pattern getPattern() {
        return PatternUtils.parsePattern(pattern);
    }

    public Pattern getPattern(@NotNull World world) {
        return PatternUtils.parsePattern(pattern, world);
    }

    public Pattern getPattern(@NotNull com.sk89q.worldedit.world.World world) {
        return PatternUtils.parsePattern(pattern, world);
    }

    public Pattern getPattern(@NotNull Extent extent) {
        return PatternUtils.parsePattern(pattern, extent);
    }

    @Override
    public @NotNull String toString() {
        return pattern;
    }

}
