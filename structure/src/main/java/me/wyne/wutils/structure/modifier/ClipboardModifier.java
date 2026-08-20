package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.session.ClipboardHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Runs first in the structure creation pipeline, on the {@link ClipboardHolder} that wraps the
 * scheme's clipboard, before any location or region is computed. This is where the paste
 * transform (rotation, flip) is chosen by combining onto {@link ClipboardHolder#getTransform()}.
 *
 * <p>Invoked directly by {@link me.wyne.wutils.structure.Structure#create}; an implementation
 * that throws propagates to the caller rather than being caught and skipped.</p>
 */
public interface ClipboardModifier {

    /**
     * Applies this modifier's transform to the clipboard in place.
     */
    void apply(@NotNull ClipboardHolder clipboardHolder);
}
