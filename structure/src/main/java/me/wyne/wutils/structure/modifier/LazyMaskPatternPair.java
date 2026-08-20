package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import me.wyne.wutils.structure.mask.MaskUtils;
import me.wyne.wutils.structure.pattern.PatternUtils;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * A mask/pattern pair kept as unparsed strings, requires WorldEdit on the classpath.
 *
 * <p>The strings are parsed on every {@code getMask}/{@code getPattern}/{@code getEager} call
 * rather than once up front, so the same pair can be resolved later against whichever world or
 * extent context is in play at the point of use. Use {@link MaskPatternPair} instead when the
 * context is already known and re-parsing on every use is unnecessary.</p>
 */
public record LazyMaskPatternPair(@NotNull String mask, @NotNull String pattern) {

    public @NotNull Mask getMask() {
        return MaskUtils.parseMask(mask);
    }

    public @NotNull Pattern getPattern() {
        return PatternUtils.parsePattern(pattern);
    }

    public @NotNull MaskPatternPair getEager() {
        return new MaskPatternPair(getMask(), getPattern());
    }

    public @NotNull Mask getMask(@NotNull World world) {
        return MaskUtils.parseMask(mask, world);
    }

    public @NotNull Pattern getPattern(@NotNull World world) {
        return PatternUtils.parsePattern(pattern, world);
    }

    public @NotNull MaskPatternPair getEager(@NotNull World world) {
        return new MaskPatternPair(getMask(world), getPattern(world));
    }

    public @NotNull Mask getMask(@NotNull com.sk89q.worldedit.world.World world) {
        return MaskUtils.parseMask(mask, world);
    }

    public @NotNull Pattern getPattern(@NotNull com.sk89q.worldedit.world.World world) {
        return PatternUtils.parsePattern(pattern, world);
    }

    public @NotNull MaskPatternPair getEager(@NotNull com.sk89q.worldedit.world.World world) {
        return new MaskPatternPair(getMask(world), getPattern(world));
    }

    public @NotNull Mask getMask(@NotNull Extent extent) {
        return MaskUtils.parseMask(mask, extent);
    }

    public @NotNull Pattern getPattern(@NotNull Extent extent) {
        return PatternUtils.parsePattern(pattern, extent);
    }

    public @NotNull MaskPatternPair getEager(@NotNull Extent extent) {
        return new MaskPatternPair(getMask(extent), getPattern(extent));
    }

    public @NotNull Mask getMask(@NotNull com.sk89q.worldedit.world.World world, @NotNull Extent extent) {
        return MaskUtils.parseMask(mask, world, extent);
    }

    public @NotNull Pattern getPattern(@NotNull com.sk89q.worldedit.world.World world, @NotNull Extent extent) {
        return PatternUtils.parsePattern(pattern, world, extent);
    }

    public @NotNull MaskPatternPair getEager(@NotNull com.sk89q.worldedit.world.World world, @NotNull Extent extent) {
        return new MaskPatternPair(getMask(world, extent), getPattern(world, extent));
    }

    @Override
    public @NotNull String toString() {
        return mask + " " + pattern;
    }

}
