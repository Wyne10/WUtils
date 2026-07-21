package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import me.wyne.wutils.structure.mask.MaskUtils;
import me.wyne.wutils.structure.pattern.PatternUtils;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public record LazyMaskPatternPair(@NotNull String mask, @NotNull String pattern) {

    public Mask getMask(@NotNull World world) {
        return MaskUtils.parseMask(mask, world);
    }

    public Pattern getPattern(@NotNull World world) {
        return PatternUtils.parsePattern(pattern, world);
    }

    public MaskPatternPair getEager(@NotNull World world) {
        return new MaskPatternPair(getMask(world), getPattern(world));
    }

    public Mask getMask(@NotNull com.sk89q.worldedit.world.World world) {
        return MaskUtils.parseMask(mask, world);
    }

    public Pattern getPattern(@NotNull com.sk89q.worldedit.world.World world) {
        return PatternUtils.parsePattern(pattern, world);
    }

    public MaskPatternPair getEager(@NotNull com.sk89q.worldedit.world.World world) {
        return new MaskPatternPair(getMask(world), getPattern(world));
    }

    @Override
    public @NotNull String toString() {
        return mask + " " + pattern;
    }

}
