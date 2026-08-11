package me.wyne.wutils.structure.modifier.edit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockCategory;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DropFloatingEditModifier extends MarginEditModifier<DropFloatingSettings> {

    private static final Set<BlockType> NEEDS_GROUND = buildVegetationSet();

    public DropFloatingEditModifier(@NotNull String key, @NotNull DropFloatingSettings value) {
        super(key, value);
    }

    public DropFloatingEditModifier(@NotNull DropFloatingSettings value) {
        super(StructureModifier.EDIT_DROP_FLOATING.getKey(), value);
    }

    @Override
    protected int margin() {
        return getValue().margin();
    }

    @Override
    protected boolean excludeFootprint() {
        return !getValue().includeClipboard();
    }

    @Override
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Region clipboardRegion) {
        World world = region.getWorld();
        Preconditions.checkNotNull(world, "Drop floating modifier region world is null");

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        int minX = min.getBlockX();
        int maxX = max.getBlockX();
        int minZ = min.getBlockZ();
        int maxZ = max.getBlockZ();
        int minY = Math.max(world.getMinY(), min.getBlockY());
        int maxY = Math.min(world.getMaxY(), max.getBlockY());

        List<BlockVector3> toRemove = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                boolean belowAir = minY - 1 < world.getMinY()
                        || editSession.getBlock(BlockVector3.at(x, minY - 1, z)).getBlockType().getMaterial().isAir();
                for (int y = minY; y <= maxY; y++) {
                    BlockType type = editSession.getBlock(BlockVector3.at(x, y, z)).getBlockType();
                    boolean removed = belowAir && NEEDS_GROUND.contains(type);
                    if (removed)
                        toRemove.add(BlockVector3.at(x, y, z));
                    // The block above rests on air when this cell is air, or when we just cleared it.
                    belowAir = removed || type.getMaterial().isAir();
                }
            }
        }

        var air = BlockTypes.AIR.getDefaultState();
        try {
            for (BlockVector3 pos : toRemove)
                editSession.setBlock(pos, air);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Drop floating modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    private static Set<BlockType> buildVegetationSet() {
        // Single-block plants that require a solid block below.
        Set<BlockType> types = new HashSet<>(Arrays.asList(
                BlockTypes.GRASS, BlockTypes.FERN, BlockTypes.DEAD_BUSH,
                BlockTypes.BROWN_MUSHROOM, BlockTypes.RED_MUSHROOM,
                BlockTypes.SWEET_BERRY_BUSH,
                // Two-block plants (both halves share the same block type, so one entry covers both).
                BlockTypes.TALL_GRASS, BlockTypes.LARGE_FERN,
                BlockTypes.SUNFLOWER, BlockTypes.LILAC, BlockTypes.ROSE_BUSH, BlockTypes.PEONY,
                // Vertically stacking plants.
                BlockTypes.CACTUS, BlockTypes.SUGAR_CANE, BlockTypes.BAMBOO, BlockTypes.BAMBOO_SAPLING,
                // Pumpkins and melons for visual aesthetics
                BlockTypes.PUMPKIN, BlockTypes.MELON,
                // Snow layers cannot survive without a block beneath them.
                BlockTypes.SNOW));
        // Flowers and saplings vary by version; pull them from tags where available.
        addCategory(types, BlockCategories.SMALL_FLOWERS);
        addCategory(types, BlockCategories.SAPLINGS);
        types.removeIf(Objects::isNull);
        return Set.copyOf(types);
    }

    private static void addCategory(@NotNull Set<BlockType> types, BlockCategory category) {
        if (category != null)
            types.addAll(category.getAll());
    }

    public static final class Factory implements AttributeFactory<DropFloatingEditModifier> {
        @Override
        public DropFloatingEditModifier create(String key, ConfigurationSection config) {
            return new DropFloatingEditModifier(key, DropFloatingSettings.parse(config.getString(key, "")));
        }
    }
}
