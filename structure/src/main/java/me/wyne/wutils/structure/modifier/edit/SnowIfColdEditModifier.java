package me.wyne.wutils.structure.modifier.edit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import me.wyne.wutils.common.plugin.PluginUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Simulates snowfall within the radius, like {@link SnowEditModifier}, but only when the biome
 * temperature at the sphere's centre is at or below {@value #SNOW_TEMPERATURE}; otherwise the
 * call is skipped entirely.
 */
public class SnowIfColdEditModifier extends RegionRadiusEditModifier {

    private static final double SNOW_TEMPERATURE = 0.15;

    public SnowIfColdEditModifier(@NotNull String key, @NotNull Double value) {
        super(key, value);
    }

    public SnowIfColdEditModifier(@NotNull Double value) {
        super(StructureModifier.EDIT_SNOW_IF_COLD.getKey(), value);
    }

    @Override
    protected void applyAt(@NotNull EditSession editSession, @NotNull Region region,
                           @NotNull BlockVector3 center, double radius) throws MaxChangedBlocksException {
        World world = region.getWorld();
        Preconditions.checkNotNull(world, "Snow if cold modifier region world is null");

        double temperature = BukkitAdapter.adapt(world).getTemperature(center.getBlockX(), center.getBlockY(), center.getBlockZ());
        if (temperature > SNOW_TEMPERATURE)
            return;

        try {
            editSession.simulateSnow(columnBase(region, center), radius, columnTop(region));
        } catch (IndexOutOfBoundsException e) {
            PluginUtils.getLogger().warn("Skipped snow simulation at {} due to a WorldEdit SnowSimulator block-state bug", center, e);
        }
    }

    public static final class Factory implements AttributeFactory<SnowIfColdEditModifier> {
        @Override
        public @NotNull SnowIfColdEditModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new SnowIfColdEditModifier(key, config.getDouble(key));
        }
    }
}
