package me.wyne.wutils.animation.data;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Describes a single Bukkit particle effect, as passed to {@link World#spawnParticle}.
 *
 * <p>A {@code null} {@code offset} given to the canonical constructor is normalized to a zero
 * vector, so {@link #offset()} itself is never {@code null}.</p>
 */
public record AnimationParticle(@NotNull Particle particle, int count, double extra, @NotNull Vector offset, @Nullable Object data) {

    public AnimationParticle(@NotNull Particle particle, int count, double extra, @Nullable Vector offset, @Nullable Object data) {
        this.particle = particle;
        this.count = count;
        this.extra = extra;
        this.offset = offset != null ? offset : new Vector(0.0, 0.0, 0.0);
        this.data = data;
    }

    public AnimationParticle(@NotNull Particle particle) {
        this(particle, 1, 0.0, new Vector(0.0, 0.0, 0.0), null);
    }

    public AnimationParticle(@NotNull Particle particle, int count) {
        this(particle, count, 0.0, new Vector(0.0, 0.0, 0.0), null);
    }

    public AnimationParticle(@NotNull Particle particle, int count, double extra) {
        this(particle, count, extra, new Vector(0.0, 0.0, 0.0), null);
    }

    public AnimationParticle(@NotNull Particle particle, int count, double extra, @Nullable Vector offset) {
        this(particle, count, extra, offset, null);
    }

    /**
     * Spawns this particle at {@code location} for all nearby players. Does nothing if
     * {@code location}'s world is not currently loaded.
     */
    public void spawnParticle(@NotNull Location location, boolean force) {
        World world = location.getWorld();
        if (world == null) return;
        world.spawnParticle(
                particle,
                location,
                count,
                offset.getX(),
                offset.getY(),
                offset.getZ(),
                extra,
                data,
                force
        );
    }

    /**
     * Spawns this particle at {@code location}, visible only to {@code receivers}. Does
     * nothing if {@code location}'s world is not currently loaded.
     */
    public void spawnParticle(@NotNull Location location, @NotNull List<@NotNull Player> receivers, @NotNull Player source, boolean force) {
        World world = location.getWorld();
        if (world == null) return;
        world.spawnParticle(
                particle,
                receivers,
                source,
                location.getX(),
                location.getY(),
                location.getZ(),
                count,
                offset.getX(),
                offset.getY(),
                offset.getZ(),
                extra,
                data,
                force
        );
    }

    /**
     * Spawns this particle at {@code vector} within {@code world}, for all nearby players.
     * Does nothing if {@code world} is {@code null}.
     */
    public void spawnParticle(@Nullable World world, @NotNull Vector vector, boolean force) {
        if (world == null) return;
        world.spawnParticle(
                particle,
                vector.getX(),
                vector.getY(),
                vector.getZ(),
                count,
                offset.getX(),
                offset.getY(),
                offset.getZ(),
                extra,
                data,
                force
        );
    }

    /**
     * Spawns this particle at {@code vector} within {@code world}, visible only to
     * {@code receivers}. Does nothing if {@code world} is {@code null}.
     */
    public void spawnParticle(@Nullable World world, @NotNull Vector vector, @NotNull List<@NotNull Player> receivers, @NotNull Player source, boolean force) {
        if (world == null) return;
        world.spawnParticle(
                particle,
                receivers,
                source,
                vector.getX(),
                vector.getY(),
                vector.getZ(),
                count,
                offset.getX(),
                offset.getY(),
                offset.getZ(),
                extra,
                data,
                force
        );
    }

}
