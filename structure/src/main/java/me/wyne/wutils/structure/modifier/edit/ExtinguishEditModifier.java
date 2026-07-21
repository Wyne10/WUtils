package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ExtinguishEditModifier extends RadiusEditModifier {

    public ExtinguishEditModifier(@NotNull String key, @NotNull Double value) {
        super(key, value);
    }

    public ExtinguishEditModifier(@NotNull Double value) {
        super(StructureModifier.EDIT_EXTINGUISH.getKey(), value);
    }

    @Override
    protected void applyAt(@NotNull EditSession editSession, @NotNull Region region,
                           @NotNull BlockVector3 center, double radius) throws MaxChangedBlocksException {
        editSession.removeNear(center, new BlockTypeMask(editSession, BlockTypes.FIRE), (int) radius);
    }

    public static final class Factory implements AttributeFactory<ExtinguishEditModifier> {
        @Override
        public ExtinguishEditModifier create(String key, ConfigurationSection config) {
            return new ExtinguishEditModifier(key, config.getDouble(key));
        }
    }
}
