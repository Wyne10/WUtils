package me.wyne.wutils.structure;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public record IntermediateStructure(@NotNull String uniqueKey,
                                    @NotNull Clipboard clipboard,
                                    @NotNull Location location,
                                    @NotNull ProtectedCuboidRegion region,
                                    @NotNull Region clipboardRegion,
                                    @NotNull Transform transform,
                                    long elapsedMillis) {
}
