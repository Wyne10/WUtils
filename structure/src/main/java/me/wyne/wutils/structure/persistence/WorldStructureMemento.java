package me.wyne.wutils.structure.persistence;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.jetbrains.annotations.NotNull;

public record WorldStructureMemento(
        @NotNull String uniqueKey,
        @NotNull Location location,
        @NotNull ProtectedCuboidRegion region,
        @NotNull Region clipboardRegion,
        @NotNull Transform transform,
        @NotNull Clipboard clipboard,
        @NotNull Clipboard snapshot) {
}
