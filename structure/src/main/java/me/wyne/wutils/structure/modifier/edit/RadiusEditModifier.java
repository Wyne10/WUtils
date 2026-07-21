package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.EditSessionModifier;
import org.jetbrains.annotations.NotNull;

public abstract class RadiusEditModifier extends ConfigurableAttribute<Double> implements EditSessionModifier {

    protected RadiusEditModifier(@NotNull String key, @NotNull Double value) {
        super(key, value);
    }

    @Override
    public void apply(@NotNull EditSession editSession, @NotNull Region region) {
        BlockVector3 center = region.getCenter().toBlockPoint();
        try {
            applyAt(editSession, region, center, getValue());
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    protected abstract void applyAt(@NotNull EditSession editSession, @NotNull Region region,
                                    @NotNull BlockVector3 center, double radius) throws MaxChangedBlocksException;
}
