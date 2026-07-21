package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class SnowEditModifier extends RegionRadiusEditModifier {

    public SnowEditModifier(@NotNull String key, @NotNull Double value) {
        super(key, value);
    }

    public SnowEditModifier(@NotNull Double value) {
        super(StructureModifier.EDIT_SNOW.getKey(), value);
    }

    @Override
    protected void applyAt(@NotNull EditSession editSession, @NotNull Region region,
                           @NotNull BlockVector3 center, double radius) throws MaxChangedBlocksException {
        editSession.simulateSnow(center, radius);
    }

    public static final class Factory implements AttributeFactory<SnowEditModifier> {
        @Override
        public SnowEditModifier create(String key, ConfigurationSection config) {
            return new SnowEditModifier(key, config.getDouble(key));
        }
    }
}
