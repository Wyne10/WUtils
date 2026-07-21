package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.world.World;
import org.jetbrains.annotations.NotNull;

public interface SnapshotModifier {
    void apply(@NotNull ForwardExtentCopy forwardExtentCopy, @NotNull World world);
}
