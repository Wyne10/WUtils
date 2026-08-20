package me.wyne.wutils.structure.region.condition;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.common.comparator.IntComparator;
import me.wyne.wutils.common.location.LocationUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.structure.IntermediateStructure;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Bounds the difference between the highest and lowest ocean-floor terrain height sampled
 * across the structure's footprint, to reject placements over uneven ground.
 */
public record AltitudeDifferenceCondition(@NotNull IntComparator comparator) implements RegionCondition {
    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "altitude-difference", comparator)
                .buildNoTrail();
    }

    /**
     * Samples the ocean-floor height at the structure's origin, its footprint corners, and
     * the midpoints between them, then compares the difference between the highest and
     * lowest sample against {@link #comparator()}.
     */
    @Override
    public boolean isValid(@NotNull IntermediateStructure intermediateStructure, @NotNull ProtectedCuboidRegion region) {
        var clipboardRegion = intermediateStructure.clipboardRegion();
        var dimensions = clipboardRegion.getMaximumPoint().subtract(clipboardRegion.getMinimumPoint());

        Location minimumPoint = new Location(
                intermediateStructure.location().getWorld(),
                clipboardRegion.getMinimumPoint().getX(),
                clipboardRegion.getMinimumPoint().getY(),
                clipboardRegion.getMinimumPoint().getZ()
        ).toHighestLocation(HeightMap.OCEAN_FLOOR);

        Location xCorner = minimumPoint.clone()
                .add(dimensions.getX(), 0.0, 0.0)
                .toHighestLocation(HeightMap.OCEAN_FLOOR);

        Location zCorner = minimumPoint.clone()
                .add(0.0, 0.0, dimensions.getZ())
                .toHighestLocation(HeightMap.OCEAN_FLOOR);

        Location xzCorner = minimumPoint.clone()
                .add(dimensions.getX(), 0.0, dimensions.getZ())
                .toHighestLocation(HeightMap.OCEAN_FLOOR);

        Location xMidpoint = getMidpoint(minimumPoint, xCorner)
                .toHighestLocation(HeightMap.OCEAN_FLOOR);

        Location zMidpoint = getMidpoint(minimumPoint, zCorner)
                .toHighestLocation(HeightMap.OCEAN_FLOOR);

        Location xzMidpoint = getMidpoint(xCorner, xzCorner)
                .toHighestLocation(HeightMap.OCEAN_FLOOR);

        Location zxMidpoint = getMidpoint(zCorner, xzCorner)
                .toHighestLocation(HeightMap.OCEAN_FLOOR);

        Set<Location> locations = new HashSet<>(Arrays.asList(
                intermediateStructure.location(), minimumPoint, xCorner, zCorner, xzCorner,
                xMidpoint, zMidpoint, xzMidpoint, zxMidpoint
        ));

        List<Integer> points = new ArrayList<>();
        for (Location location : locations) {
            points.add(location.getBlockY());
        }

        int max = Collections.max(points);
        int min = Collections.min(points);

        return comparator.compare(max - min);
    }

    private @NotNull Location getMidpoint(@NotNull Location first, @NotNull Location second) {
        var midpoint = first.toVector().getMidpoint(second.toVector());
        return LocationUtils.of(first.getWorld(), midpoint);
    }
}
