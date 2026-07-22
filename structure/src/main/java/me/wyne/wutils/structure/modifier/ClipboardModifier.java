package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.session.ClipboardHolder;
import org.jetbrains.annotations.NotNull;

public interface ClipboardModifier {
    void apply(@NotNull ClipboardHolder clipboardHolder);
}
