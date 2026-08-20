package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.regions.Region;
import org.jetbrains.annotations.NotNull;

/**
 * Runs after the paste, on a fresh {@link EditSession}, to reshape the terrain surrounding the
 * pasted structure (e.g. margin smoothing, snow, deforestation).
 *
 * <p>Applied by {@link me.wyne.wutils.structure.WorldStructure}; an implementation that throws is
 * logged and skipped rather than propagated.</p>
 */
public interface EditSessionModifier {

    /**
     * Applies this modifier's edits to {@code editSession} within {@code region}.
     */
    void apply(@NotNull EditSession editSession, @NotNull Region region);
}
