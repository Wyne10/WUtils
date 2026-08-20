package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Configures the {@link PasteBuilder} used to paste the clipboard into the world.
 *
 * <p>Applied by {@link me.wyne.wutils.structure.WorldStructure}; an implementation that throws is
 * logged and skipped rather than propagated.</p>
 */
public interface PasteModifier {

    /**
     * Applies this modifier's settings to {@code pasteBuilder} in place.
     */
    void apply(@NotNull PasteBuilder pasteBuilder, @NotNull World world);
}
