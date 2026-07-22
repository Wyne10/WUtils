package me.wyne.wutils.structure.modifier;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface LocationModifier {
    @NotNull Location apply(@NotNull Location location);
}
