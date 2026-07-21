package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import me.wyne.wutils.structure.mask.MaskUtils;
import me.wyne.wutils.structure.pattern.PatternUtils;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public record MaskPatternPair(@NotNull Mask mask, @NotNull Pattern pattern) {

    public MaskPatternPair(@NotNull String mask, @NotNull String pattern, @NotNull World world) {
        this(MaskUtils.parseMask(mask, world), PatternUtils.parsePattern(pattern, world));
    }

}
