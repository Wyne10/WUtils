package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import me.wyne.wutils.structure.mask.MaskUtils;
import me.wyne.wutils.structure.pattern.PatternUtils;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * An already-resolved {@link Mask}/{@link Pattern} pair, requires WorldEdit on the classpath.
 *
 * <p>Eager counterpart to {@link LazyMaskPatternPair}: use this when the mask and pattern strings
 * can be parsed once, up front, against a known world or extent.</p>
 */
public record MaskPatternPair(@NotNull Mask mask, @NotNull Pattern pattern) {

    public MaskPatternPair(@NotNull String mask, @NotNull String pattern, @NotNull World world) {
        this(MaskUtils.parseMask(mask, world), PatternUtils.parsePattern(pattern, world));
    }

    public MaskPatternPair(@NotNull String mask, @NotNull String pattern, @NotNull com.sk89q.worldedit.world.World world, @NotNull Extent extent) {
        this(MaskUtils.parseMask(mask, world, extent), PatternUtils.parsePattern(pattern, world, extent));
    }

}
