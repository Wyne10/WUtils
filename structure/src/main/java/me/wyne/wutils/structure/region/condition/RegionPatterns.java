package me.wyne.wutils.structure.region.condition;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wyne.wutils.structure.IntermediateStructure;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * WorldGuard region ids to match against, as regular expressions. Each pattern must match the
 * whole region id, so a plain id still matches only itself.
 */
public record RegionPatterns(@NotNull List<@NotNull Pattern> patterns) {
    public RegionPatterns {
        patterns = List.copyOf(patterns);
    }

    public static @NotNull RegionPatterns of(@NotNull Collection<@NotNull String> regexes) {
        return new RegionPatterns(regexes.stream().map(Pattern::compile).toList());
    }

    public boolean matches(@NotNull String regionId) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(regionId).matches());
    }

    public @NotNull List<@NotNull String> toStrings() {
        return patterns.stream().map(Pattern::pattern).toList();
    }

    /**
     * Returns the existing WorldGuard regions overlapping {@code region}, or only the
     * highest-priority one of them when {@code top} is set. Empty when nothing overlaps.
     */
    static @NotNull Stream<@NotNull ProtectedRegion> overlappingRegions(@NotNull IntermediateStructure intermediateStructure,
                                                                        @NotNull ProtectedCuboidRegion region,
                                                                        boolean top) {
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Preconditions.checkNotNull(intermediateStructure.clipboardRegion().getWorld(), "Intermediate structure " + intermediateStructure.uniqueKey() + " clipboard region world was null");
        var regions = container.get(intermediateStructure.clipboardRegion().getWorld());
        Preconditions.checkNotNull(regions, "Regions manager for " + intermediateStructure.uniqueKey() + " was null");
        var applicableRegions = regions.getApplicableRegions(region).getRegions().stream();
        if (!top)
            return applicableRegions;
        return applicableRegions.max(Comparator.comparingInt(ProtectedRegion::getPriority)).stream();
    }
}
