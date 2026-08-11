package me.wyne.wutils.structure.modifier.edit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.convolution.HeightMap;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.mask.MaskUtils;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class GrowEditModifier extends MarginEditModifier<GrowSettings> {

    public GrowEditModifier(@NotNull String key, @NotNull GrowSettings value) {
        super(key, value);
    }

    public GrowEditModifier(@NotNull GrowSettings value) {
        super(StructureModifier.EDIT_GROW.getKey(), value);
    }

    @Override
    protected int margin() {
        return getValue().margin();
    }

    @Override
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region,
                             @NotNull Region clipboardRegion) {
        var settings = getValue();
        Preconditions.checkNotNull(region.getWorld(), "Grow modifier region world is null");
        Mask mask = settings.mask() == null ? null : MaskUtils.parseMask(settings.mask(), region.getWorld(), editSession);

        var regionMin = region.getMinimumPoint();
        var regionMax = region.getMaximumPoint();
        int minX = regionMin.getBlockX();
        int minY = regionMin.getBlockY();
        int minZ = regionMin.getBlockZ();
        int maxX = regionMax.getBlockX();
        int maxY = regionMax.getBlockY();
        int maxZ = regionMax.getBlockZ();
        int width = region.getWidth();

        var structMin = clipboardRegion.getMinimumPoint();
        var structMax = clipboardRegion.getMaximumPoint();
        int structMinX = structMin.getBlockX();
        int structMaxX = structMax.getBlockX();
        int structMinZ = structMin.getBlockZ();
        int structMaxZ = structMax.getBlockZ();

        int baseY = settings.base().evaluate(structMin.getBlockY());
        double margin = Math.max(1, settings.margin());
        double strength = settings.strength();

        HeightMap heightMap = new HeightMap(editSession, region, mask);
        int[] target = new int[width * region.getLength()];

        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                int index = (z - minZ) * width + (x - minX);
                // Matches HeightMap's own source-height computation exactly (same call + mask).
                int natural = editSession.getHighestTerrainBlock(x, z, minY, maxY, mask);

                int dx = x < structMinX ? structMinX - x : Math.max(0, x - structMaxX);
                int dz = z < structMinZ ? structMinZ - z : Math.max(0, z - structMaxZ);
                if (dx == 0 && dz == 0) {          // inside the footprint — leave untouched
                    target[index] = natural;
                    continue;
                }

                double distance = Math.sqrt((double) dx * dx + (double) dz * dz);
                double falloff = Math.pow(Math.max(0.0, 1.0 - distance / margin), strength);
                int blended = (int) Math.round(natural + (baseY - natural) * falloff);
                target[index] = switch (settings.direction()) {
                    case UP -> Math.max(natural, blended);     // only grow land up toward the base
                    case DOWN -> Math.min(natural, blended);   // only slope hills down toward the base
                    case BOTH -> blended;                      // fill dips up and cut hills down
                };
            }
        }

        try {
            heightMap.apply(target);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Grow modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    public static final class Factory implements AttributeFactory<GrowEditModifier> {
        @Override
        public GrowEditModifier create(String key, ConfigurationSection config) {
            return new GrowEditModifier(key, GrowSettings.parse(config.getString(key, "")));
        }
    }
}
