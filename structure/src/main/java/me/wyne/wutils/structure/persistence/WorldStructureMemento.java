package me.wyne.wutils.structure.persistence;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.jetbrains.annotations.NotNull;

/**
 * The persistable state of a spawned {@link me.wyne.wutils.structure.WorldStructure} — everything
 * {@link me.wyne.wutils.structure.WorldStructure#capture()} needs to save and
 * {@link me.wyne.wutils.structure.WorldStructure#restore} needs to rebuild it, including the
 * pre-spawn region {@code snapshot} used to undo the paste. Serialized to JSON by
 * {@link WorldStructureMementoSerializer} via {@link WorldStructurePersistence}.
 */
public record WorldStructureMemento(
        @NotNull String uniqueKey,
        @NotNull Location location,
        @NotNull ProtectedCuboidRegion region,
        @NotNull Region clipboardRegion,
        @NotNull Transform transform,
        @NotNull Clipboard clipboard,
        @NotNull Clipboard snapshot) {
}
