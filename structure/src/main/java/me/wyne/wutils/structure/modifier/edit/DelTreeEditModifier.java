package me.wyne.wutils.structure.modifier.edit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class DelTreeEditModifier extends MarginEditModifier<DelTreeSettings> {

    // Same traversal range as WorldEdit's FloatingTreeRemover.
    private static final int RANGE_SQ = 10000;
    private static final BlockVector3[] DIRECTIONS = {
            BlockVector3.at(0, 0, -1),
            BlockVector3.at(1, 0, 0),
            BlockVector3.at(0, 0, 1),
            BlockVector3.at(-1, 0, 0),
            BlockVector3.at(0, 1, 0),
            BlockVector3.at(0, -1, 0),
    };

    public DelTreeEditModifier(@NotNull String key, @NotNull DelTreeSettings value) {
        super(key, value);
    }

    public DelTreeEditModifier(@NotNull DelTreeSettings value) {
        super(StructureModifier.EDIT_DELTREE.getKey(), value);
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
        Preconditions.checkNotNull(world, "Deltree modifier region world is null");

        Predicate<BlockVector3> inScope = getValue().includeClipboard()
                ? pos -> true
                : pos -> !clipboardRegion.contains(pos);

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        int minX = min.getBlockX();
        int maxX = max.getBlockX();
        int minZ = min.getBlockZ();
        int maxZ = max.getBlockZ();
        int minY = Math.max(world.getMinY(), min.getBlockY());
        int maxY = Math.min(world.getMaxY(), max.getBlockY());

        Set<BlockVector3> processed = new HashSet<>();
        List<BlockVector3> toRemove = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = maxY; y >= minY; y--) {
                    BlockVector3 pos = BlockVector3.at(x, y, z);
                    if (processed.contains(pos) || !inScope.test(pos) || !isTreeBlock(editSession.getBlock(pos).getBlockType()))
                        continue;
                    Set<BlockVector3> tree = new HashSet<>();
                    if (isFloating(editSession, pos, inScope, tree))
                        toRemove.addAll(tree);
                    processed.addAll(tree);
                }
            }
        }

        try {
            var air = BlockTypes.AIR.getDefaultState();
            for (BlockVector3 pos : toRemove)
                editSession.setBlock(pos, air);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Deltree modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    private static boolean isFloating(@NotNull EditSession editSession, @NotNull BlockVector3 origin,
                                      @NotNull Predicate<BlockVector3> inScope, @NotNull Set<BlockVector3> tree) {
        Set<BlockVector3> visited = new HashSet<>();
        Deque<BlockVector3> queue = new ArrayDeque<>();
        queue.addLast(origin);
        visited.add(origin);
        tree.add(origin);
        boolean floating = true;
        while (!queue.isEmpty()) {
            BlockVector3 current = queue.removeFirst();
            BlockType currentType = editSession.getBlock(current).getBlockType();
            // Leaves and vines may rest on the ground without anchoring the tree; only trunks do.
            boolean currentIsTrunk = !(BlockCategories.LEAVES.contains(currentType) || currentType == BlockTypes.VINE);
            for (BlockVector3 direction : DIRECTIONS) {
                BlockVector3 next = current.add(direction);
                if (origin.distanceSq(next) > RANGE_SQ || !visited.add(next))
                    continue;
                if (!inScope.test(next))
                    continue;
                BlockType type = editSession.getBlock(next).getBlockType();
                if (type.getMaterial().isAir() || type == BlockTypes.SNOW)
                    continue;
                if (isTreeBlock(type)) {
                    tree.add(next);
                    queue.addLast(next);
                } else if (currentIsTrunk) {
                    floating = false;
                }
            }
        }
        return floating;
    }

    private static boolean isTreeBlock(@NotNull BlockType type) {
        return BlockCategories.LEAVES.contains(type)
                || BlockCategories.LOGS.contains(type)
                || type == BlockTypes.RED_MUSHROOM_BLOCK
                || type == BlockTypes.BROWN_MUSHROOM_BLOCK
                || type == BlockTypes.MUSHROOM_STEM
                || type == BlockTypes.VINE;
    }

    public static final class Factory implements AttributeFactory<DelTreeEditModifier> {
        @Override
        public DelTreeEditModifier create(String key, ConfigurationSection config) {
            return new DelTreeEditModifier(key, DelTreeSettings.parse(config.getString(key, "")));
        }
    }
}
