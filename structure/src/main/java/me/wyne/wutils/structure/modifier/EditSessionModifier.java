package me.wyne.wutils.structure.modifier;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.regions.Region;
import org.jetbrains.annotations.NotNull;

public interface EditSessionModifier {
    void apply(@NotNull EditSession editSession, @NotNull Region region);
}
