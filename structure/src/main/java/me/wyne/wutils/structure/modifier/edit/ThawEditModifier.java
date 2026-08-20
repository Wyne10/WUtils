package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Melts snow and ice within the radius via {@link EditSession#thaw}, scanning the full height
 * column over the region.
 */
public class ThawEditModifier extends RegionRadiusEditModifier {

    public ThawEditModifier(@NotNull String key, @NotNull Double value) {
        super(key, value);
    }

    public ThawEditModifier(@NotNull Double value) {
        super(StructureModifier.EDIT_THAW.getKey(), value);
    }

    @Override
    protected void applyAt(@NotNull EditSession editSession, @NotNull Region region,
                           @NotNull BlockVector3 center, double radius) throws MaxChangedBlocksException {
        editSession.thaw(columnBase(region, center), radius, columnTop(region));
    }

    public static final class Factory implements AttributeFactory<ThawEditModifier> {
        @Override
        public @NotNull ThawEditModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new ThawEditModifier(key, config.getDouble(key));
        }
    }
}
