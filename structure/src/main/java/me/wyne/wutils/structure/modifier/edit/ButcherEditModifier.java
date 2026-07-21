package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ButcherEditModifier extends RadiusEditModifier {

    public ButcherEditModifier(@NotNull String key, @NotNull Double value) {
        super(key, value);
    }

    public ButcherEditModifier(@NotNull Double value) {
        super(StructureModifier.EDIT_BUTCHER.getKey(), value);
    }

    @Override
    protected void applyAt(@NotNull EditSession editSession, @NotNull Region region,
                           @NotNull BlockVector3 center, double radius) {
        var sphere = new EllipsoidRegion(center, Vector3.at(radius, radius, radius));
        for (Entity entity : editSession.getEntities(sphere)) {
            BaseEntity state = entity.getState();
            if (state == null || isSpared(state.getType()))
                continue;
            entity.remove();
        }
    }

    private static boolean isSpared(EntityType type) {
        return type == EntityTypes.PLAYER
                || type == EntityTypes.ARMOR_STAND
                || type == EntityTypes.ITEM_FRAME
                || type == EntityTypes.PAINTING;
    }

    public static final class Factory implements AttributeFactory<ButcherEditModifier> {
        @Override
        public ButcherEditModifier create(String key, ConfigurationSection config) {
            return new ButcherEditModifier(key, config.getDouble(key));
        }
    }
}
