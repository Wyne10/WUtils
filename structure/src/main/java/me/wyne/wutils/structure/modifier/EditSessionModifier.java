package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.regions.Region;
import org.jetbrains.annotations.NotNull;

/**
 * Runs after the paste, on a fresh {@link EditSession}, to edit blocks, biomes or entities inside
 * the pasted structure, around it, or both, depending on the implementation (e.g. block
 * replacement, margin smoothing, snow).
 *
 * <p>Applied by {@link me.wyne.wutils.structure.WorldStructure}; an implementation that throws is
 * logged and skipped rather than propagated.</p>
 */
public interface EditSessionModifier {

    /**
     * Applies this modifier's edits to {@code editSession}. {@code region} is the pasted
     * structure's region, which implementations may work inside of, expand beyond, or exclude.
     */
    void apply(@NotNull EditSession editSession, @NotNull Region region);
}
