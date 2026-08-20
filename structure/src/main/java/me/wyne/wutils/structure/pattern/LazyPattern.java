package me.wyne.wutils.structure.pattern;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.Pattern;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * A pattern kept as an unparsed string, requires WorldEdit on the classpath.
 *
 * <p>The string is parsed on every {@code getPattern} call rather than once up front, so the
 * same pattern can be resolved later against whichever world or extent context is in play at the
 * point of use.</p>
 */
public record LazyPattern(@NotNull String pattern) {

    public @NotNull Pattern getPattern() {
        return PatternUtils.parsePattern(pattern);
    }

    public @NotNull Pattern getPattern(@NotNull World world) {
        return PatternUtils.parsePattern(pattern, world);
    }

    public @NotNull Pattern getPattern(@NotNull com.sk89q.worldedit.world.World world) {
        return PatternUtils.parsePattern(pattern, world);
    }

    public @NotNull Pattern getPattern(@NotNull Extent extent) {
        return PatternUtils.parsePattern(pattern, extent);
    }

    public @NotNull Pattern getPattern(@NotNull com.sk89q.worldedit.world.World world, @NotNull Extent extent) {
        return PatternUtils.parsePattern(pattern, world, extent);
    }

    @Override
    public @NotNull String toString() {
        return pattern;
    }

}
