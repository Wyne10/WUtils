package me.wyne.wutils.common.world;

import me.wyne.wutils.common.range.ClosedIntRange;
import me.wyne.wutils.common.range.VectorRange;
import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.concurrent.CompletableFuture;

public final class WorldUtils {

    public static final ClosedIntRange CHUNK_X_RANGE = new ClosedIntRange(0, 15);
    public static final ClosedIntRange CHUNK_Y_RANGE = new ClosedIntRange(0, 255);
    public static final ClosedIntRange CHUNK_Z_RANGE = new ClosedIntRange(0, 15);
    public static final VectorRange CHUNK_RANGE = new VectorRange(VectorUtils.zero(), new Vector(CHUNK_X_RANGE.getMax(), CHUNK_Y_RANGE.getMax(), CHUNK_Z_RANGE.getMax()));

    public static CompletableFuture<Block> getHighestBlockAtAsync(World world, int x, int z) {
        return world.getChunkAtAsync(x >> 4, z >> 4)
                .thenApply(chunk -> world.getHighestBlockAt(x, z));
    }

    public static CompletableFuture<Location> getHighestLocationAtAsync(World world, int x, int z) {
        return world.getChunkAtAsync(x >> 4, z >> 4)
                .thenApply(chunk -> world.getHighestBlockAt(x, z).getLocation());
    }

    public static CompletableFuture<Block> getHighestBlockAtAsync(World world, double x, double z) {
        return getHighestBlockAtAsync(world, NumberConversions.floor(x), NumberConversions.floor(z));
    }

    public static CompletableFuture<Location> getHighestLocationAtAsync(World world, double x, double z) {
        return getHighestLocationAtAsync(world, NumberConversions.floor(x), NumberConversions.floor(z));
    }

    public static CompletableFuture<Block> getHighestBlockAtAsync(World world, Vector vector) {
        return getHighestBlockAtAsync(world, vector.getBlockX(), vector.getBlockZ());
    }

    public static CompletableFuture<Location> getHighestLocationAtAsync(World world, Vector vector) {
        return getHighestLocationAtAsync(world, vector.getBlockX(), vector.getBlockZ());
    }

    public static CompletableFuture<Block> getHighestBlockAtAsync(Location location) {
        return getHighestBlockAtAsync(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    public static CompletableFuture<Location> getHighestLocationAtAsync(Location location) {
        return getHighestLocationAtAsync(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    public static Vector getRandomChunkPoint() {
        return CHUNK_RANGE.getRandom();
    }

    public static Vector getRandomChunkPoint2D() {
        return new Vector(CHUNK_X_RANGE.getRandom(), 0, CHUNK_Z_RANGE.getRandom());
    }

}
