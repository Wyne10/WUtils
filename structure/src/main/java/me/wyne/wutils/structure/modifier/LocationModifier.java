package me.wyne.wutils.structure.modifier;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Runs in the structure creation pipeline after the highest-block lookup, adjusting the
 * placement {@link Location} before the region and clipboard geometry are computed against it.
 *
 * <p>Invoked directly by {@link me.wyne.wutils.structure.Structure#create}; an implementation
 * that throws propagates to the caller rather than being caught and skipped.</p>
 */
public interface LocationModifier {

    /**
     * Returns the adjusted placement location. Does not mutate {@code location}.
     */
    @NotNull Location apply(@NotNull Location location);
}
