package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

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
        editSession.thaw(center, radius);
    }

    public static final class Factory implements AttributeFactory<ThawEditModifier> {
        @Override
        public ThawEditModifier create(String key, ConfigurationSection config) {
            return new ThawEditModifier(key, config.getDouble(key));
        }
    }
}
