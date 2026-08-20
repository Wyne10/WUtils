package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.common.plugin.PluginUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Simulates snowfall within the radius via {@link EditSession#simulateSnow}, scanning the full
 * height column over the region. An {@link IndexOutOfBoundsException} from a known WorldEdit
 * snow-simulator bug is caught and logged rather than propagated, so a single bad column does not
 * abort the rest of the structure's edits.
 */
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
        try {
            editSession.simulateSnow(columnBase(region, center), radius, columnTop(region));
        } catch (IndexOutOfBoundsException e) {
            PluginUtils.getLogger().warn("Skipped snow simulation at {} due to a WorldEdit SnowSimulator block-state bug", center, e);
        }
    }

    public static final class Factory implements AttributeFactory<SnowEditModifier> {
        @Override
        public @NotNull SnowEditModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new SnowEditModifier(key, config.getDouble(key));
        }
    }
}
