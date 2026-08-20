package me.wyne.wutils.common.world;

import me.wyne.wutils.common.range.ClosedIntRange;
import me.wyne.wutils.common.range.VectorRange;
import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * World/chunk helpers: async highest-block lookups that load the target chunk first, chunk
 * coordinate ranges, and random chunk-local points.
 */
public final class WorldUtils {

    /** Matches biome enum names describing elevated terrain, used by {@link BiomePreset#HIGHLAND}. */
    public static final @NotNull Pattern HIGHLAND_REGEX = Pattern.compile(".*(?:MOUNTAINS|HILLS|PEAKS|SLOPES|PLATEAU|ERODED).*");

    public static final @NotNull ClosedIntRange CHUNK_X_RANGE = new ClosedIntRange(0, 15);
    public static final @NotNull ClosedIntRange CHUNK_Y_RANGE = new ClosedIntRange(0, 255);
    public static final @NotNull ClosedIntRange CHUNK_Z_RANGE = new ClosedIntRange(0, 15);
    /** The full in-chunk coordinate volume, x/z in [0, 15] and y in [0, 255]. */
    public static final @NotNull VectorRange CHUNK_RANGE = new VectorRange(VectorUtils.zero(), new Vector(CHUNK_X_RANGE.getMax(), CHUNK_Y_RANGE.getMax(), CHUNK_Z_RANGE.getMax()));

    /**
     * Loads the chunk containing block coordinates ({@code x}, {@code z}) asynchronously, then
     * resolves to the highest block there. Safe to call off the main thread; the completion
     * itself still runs on the server thread per {@link World#getChunkAtAsync}.
     */
    public static @NotNull CompletableFuture<@NotNull Block> getHighestBlockAtAsync(@NotNull World world, int x, int z) {
        return world.getChunkAtAsync(x >> 4, z >> 4)
                .thenApply(chunk -> world.getHighestBlockAt(x, z));
    }

    /** @see #getHighestBlockAtAsync(World, int, int) */
    public static @NotNull CompletableFuture<@NotNull Location> getHighestLocationAtAsync(@NotNull World world, int x, int z) {
        return world.getChunkAtAsync(x >> 4, z >> 4)
                .thenApply(chunk -> world.getHighestBlockAt(x, z).getLocation());
    }

    /** @see #getHighestBlockAtAsync(World, int, int) */
    public static @NotNull CompletableFuture<@NotNull Block> getHighestBlockAtAsync(@NotNull World world, double x, double z) {
        return getHighestBlockAtAsync(world, NumberConversions.floor(x), NumberConversions.floor(z));
    }

    /** @see #getHighestBlockAtAsync(World, int, int) */
    public static @NotNull CompletableFuture<@NotNull Location> getHighestLocationAtAsync(@NotNull World world, double x, double z) {
        return getHighestLocationAtAsync(world, NumberConversions.floor(x), NumberConversions.floor(z));
    }

    /** @see #getHighestBlockAtAsync(World, int, int) */
    public static @NotNull CompletableFuture<@NotNull Block> getHighestBlockAtAsync(@NotNull World world, @NotNull Vector vector) {
        return getHighestBlockAtAsync(world, vector.getBlockX(), vector.getBlockZ());
    }

    /** @see #getHighestBlockAtAsync(World, int, int) */
    public static @NotNull CompletableFuture<@NotNull Location> getHighestLocationAtAsync(@NotNull World world, @NotNull Vector vector) {
        return getHighestLocationAtAsync(world, vector.getBlockX(), vector.getBlockZ());
    }

    /** @see #getHighestBlockAtAsync(World, int, int) */
    public static @NotNull CompletableFuture<@NotNull Block> getHighestBlockAtAsync(@NotNull Location location) {
        return getHighestBlockAtAsync(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    /** @see #getHighestBlockAtAsync(World, int, int) */
    public static @NotNull CompletableFuture<@NotNull Location> getHighestLocationAtAsync(@NotNull Location location) {
        return getHighestLocationAtAsync(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    /** @return a uniformly random point within {@link #CHUNK_RANGE} (all three axes) */
    public static @NotNull Vector getRandomChunkPoint() {
        return CHUNK_RANGE.getRandom();
    }

    /** @return a uniformly random point within a chunk's x/z bounds, with y fixed at 0 */
    public static @NotNull Vector getRandomChunkPoint2D() {
        return new Vector(CHUNK_X_RANGE.getRandom(), 0, CHUNK_Z_RANGE.getRandom());
    }

}
