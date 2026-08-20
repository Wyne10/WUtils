package me.wyne.wutils.structure;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A resolved candidate placement, produced mid-{@link Structure#create} once a location and clipboard
 * region have passed all {@code LocationCondition}s but before {@code RegionCondition}s and
 * {@code RegionModifier}s have been applied to {@code region}.
 *
 * @param region          the WorldGuard protection region before {@code RegionModifier}s run
 * @param clipboardRegion the clipboard's own region, mapped into world coordinates
 * @param elapsedMillis   time spent so far in the current {@link Structure#create} call, in
 *                        milliseconds
 */
public record IntermediateStructure(@NotNull String uniqueKey,
                                    @NotNull Clipboard clipboard,
                                    @NotNull Location location,
                                    @NotNull ProtectedCuboidRegion region,
                                    @NotNull Region clipboardRegion,
                                    @NotNull Transform transform,
                                    long elapsedMillis) {
}
