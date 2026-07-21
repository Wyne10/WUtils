package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.jetbrains.annotations.NotNull;

public interface RegionModifier {
    @NotNull ProtectedCuboidRegion apply(@NotNull ProtectedCuboidRegion region, @NotNull Region clipboardRegion);
}
