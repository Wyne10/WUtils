package me.wyne.wutils.structure.modifier.edit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.mask.MaskUtils;
import me.wyne.wutils.structure.modifier.EditSessionModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Replaces the pasted structure's surface blocks with the block type most common in the
 * surrounding terrain, sampled column-by-column within {@link AdaptSurfaceSettings#margin()}.
 * Unlike the {@link MarginEditModifier} family it only reads the surroundings and never writes
 * to them, so it does not need to grow the WorldGuard protected region.
 */
public class AdaptSurfaceEditModifier extends ConfigurableAttribute<AdaptSurfaceSettings> implements EditSessionModifier {

    public AdaptSurfaceEditModifier(@NotNull String key, @NotNull AdaptSurfaceSettings value) {
        super(key, value);
    }

    public AdaptSurfaceEditModifier(@NotNull AdaptSurfaceSettings value) {
        super(StructureModifier.EDIT_ADAPT_SURFACE.getKey(), value);
    }

    @Override
    public void apply(@NotNull EditSession editSession, @NotNull Region region) {
        World world = region.getWorld();
        Preconditions.checkNotNull(world, "Adapt surface modifier region world is null");
        var settings = getValue();

        Mask replaceMask = MaskUtils.parseMask(settings.mask(), world, editSession);
        Mask sampleMask = MaskUtils.parseMask(settings.sampleMask(), world, editSession);

        BlockState mostCommon = mostCommonSurrounding(editSession, region, world, sampleMask, settings.margin());
        if (mostCommon == null)
            return;

        try {
            editSession.replaceBlocks(region, replaceMask, mostCommon);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Adapt surface modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    private static @Nullable BlockState mostCommonSurrounding(@NotNull EditSession editSession, @NotNull Region region,
                                                              @NotNull World world, @NotNull Mask sampleMask, int margin) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        int minX = min.getBlockX();
        int maxX = max.getBlockX();
        int minZ = min.getBlockZ();
        int maxZ = max.getBlockZ();
        int top = Math.min(world.getMaxY(), max.getBlockY() + margin);
        int bottom = Math.max(world.getMinY(), min.getBlockY() - margin);

        Map<BlockState, Integer> frequencies = new LinkedHashMap<>();
        for (int x = minX - margin; x <= maxX + margin; x++) {
            for (int z = minZ - margin; z <= maxZ + margin; z++) {
                boolean insideFootprint = x >= minX && x <= maxX && z >= minZ && z <= maxZ;
                if (insideFootprint)
                    continue;
                for (int y = top; y >= bottom; y--) {
                    BlockVector3 pos = BlockVector3.at(x, y, z);
                    if (sampleMask.test(pos)) {
                        frequencies.merge(editSession.getBlock(pos), 1, Integer::sum);
                        break;
                    }
                }
            }
        }

        if (frequencies.isEmpty())
            return null;

        return frequencies.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static final class Factory implements AttributeFactory<AdaptSurfaceEditModifier> {
        @Override
        public @NotNull AdaptSurfaceEditModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new AdaptSurfaceEditModifier(key, AdaptSurfaceSettings.parse(config.getString(key, "")));
        }
    }
}
