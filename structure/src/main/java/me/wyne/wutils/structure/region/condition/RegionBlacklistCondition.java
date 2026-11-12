package me.wyne.wutils.structure.region.condition;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.structure.IntermediateStructure;
import org.jetbrains.annotations.NotNull;

/**
 * Requires no WorldGuard region overlapping the structure's protected region to match any of
 * {@code patterns}. With {@code top} set, only the highest-priority overlapping region is
 * checked. Passes when nothing overlaps. Requires WorldGuard.
 */
public record RegionBlacklistCondition(@NotNull RegionPatterns patterns, boolean top) implements RegionCondition {
    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .appendCollection(depth, top ? "top-region-blacklist" : "region-blacklist", patterns.toStrings())
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull IntermediateStructure intermediateStructure, @NotNull ProtectedCuboidRegion region) {
        return RegionPatterns.overlappingRegions(intermediateStructure, region, top)
                .noneMatch(r -> patterns.matches(r.getId()));
    }
}
