package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Configures the {@link ForwardExtentCopy} that captures the pre-paste world state used to roll
 * back the structure on {@link me.wyne.wutils.structure.WorldStructure#close}.
 *
 * <p>Applied by {@link me.wyne.wutils.structure.WorldStructure}; an implementation that throws is
 * logged and skipped rather than propagated.</p>
 */
public interface SnapshotModifier {

    /**
     * Applies this modifier's settings to {@code forwardExtentCopy} in place.
     */
    void apply(@NotNull ForwardExtentCopy forwardExtentCopy, @NotNull World world);
}
