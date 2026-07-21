package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.World;
import org.jetbrains.annotations.NotNull;

public interface PasteModifier {
    void apply(@NotNull PasteBuilder pasteBuilder, @NotNull World world);
}
