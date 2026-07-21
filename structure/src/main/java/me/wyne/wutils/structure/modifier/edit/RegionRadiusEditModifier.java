package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.structure.modifier.RegionModifier;
import org.jetbrains.annotations.NotNull;

public abstract class RegionRadiusEditModifier extends RadiusEditModifier implements RegionModifier {

    protected RegionRadiusEditModifier(@NotNull String key, @NotNull Double value) {
        super(key, value);
    }

    @Override
    public @NotNull ProtectedCuboidRegion apply(@NotNull ProtectedCuboidRegion region, @NotNull Region clipboardRegion) {
        int radius = (int) Math.ceil(getValue());
        var center = clipboardRegion.getCenter().toBlockPoint();
        var sphereMin = center.subtract(radius, radius, radius);
        var sphereMax = center.add(radius, radius, radius);

        var newMin = region.getMinimumPoint().getMinimum(sphereMin);
        var newMax = region.getMaximumPoint().getMaximum(sphereMax);
        if (newMin.equals(region.getMinimumPoint()) && newMax.equals(region.getMaximumPoint()))
            return region;

        var newRegion = new ProtectedCuboidRegion(region.getId(), region.isTransient(), newMin, newMax);
        newRegion.copyFrom(region);
        return newRegion;
    }
}