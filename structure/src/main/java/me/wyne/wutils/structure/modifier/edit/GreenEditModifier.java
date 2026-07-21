package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class GreenEditModifier extends RegionRadiusEditModifier {

    public GreenEditModifier(@NotNull String key, @NotNull Double value) {
        super(key, value);
    }

    public GreenEditModifier(@NotNull Double value) {
        super(StructureModifier.EDIT_GREEN.getKey(), value);
    }

    @Override
    protected void applyAt(@NotNull EditSession editSession, @NotNull Region region,
                           @NotNull BlockVector3 center, double radius) throws MaxChangedBlocksException {
        editSession.green(center, radius, true);
    }

    public static final class Factory implements AttributeFactory<GreenEditModifier> {
        @Override
        public GreenEditModifier create(String key, ConfigurationSection config) {
            return new GreenEditModifier(key, config.getDouble(key));
        }
    }
}
