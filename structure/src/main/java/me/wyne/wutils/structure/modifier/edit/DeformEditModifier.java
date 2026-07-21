package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.expression.ExpressionException;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class DeformEditModifier extends MarginEditModifier<DeformSettings> {

    public DeformEditModifier(@NotNull String key, @NotNull DeformSettings value) {
        super(key, value);
    }

    public DeformEditModifier(@NotNull DeformSettings value) {
        super(StructureModifier.EDIT_DEFORM.getKey(), value);
    }

    @Override
    protected int margin() {
        return getValue().margin();
    }

    @Override
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Mask ringMask) {
        Vector3 min = region.getMinimumPoint().toVector3();
        Vector3 max = region.getMaximumPoint().toVector3();
        Vector3 zero = max.add(min).divide(2);
        Vector3 unit = max.subtract(zero);
        // Guard against zero-thickness axes so the expression's coordinate division is safe.
        if (unit.getX() == 0) unit = unit.withX(1);
        if (unit.getY() == 0) unit = unit.withY(1);
        if (unit.getZ() == 0) unit = unit.withZ(1);

        try {
            editSession.deformRegion(region, zero, unit, getValue().expression());
        } catch (ExpressionException e) {
            throw new IllegalArgumentException("Deform modifier '" + getKey() + "' has an invalid expression '" + getValue().expression() + "'", e);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Deform modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    public static final class Factory implements AttributeFactory<DeformEditModifier> {
        @Override
        public DeformEditModifier create(String key, ConfigurationSection config) {
            return new DeformEditModifier(key, DeformSettings.parse(config.getString(key, "")));
        }
    }
}
