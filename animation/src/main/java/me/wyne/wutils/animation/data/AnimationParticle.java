package me.wyne.wutils.animation.data;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record AnimationParticle(Particle particle, int count, double extra, Vector offset, @Nullable Object data) {

    public AnimationParticle(Particle particle, int count, double extra, Vector offset, @Nullable Object data) {
        this.particle = particle;
        this.count = count;
        this.extra = extra;
        this.offset = offset != null ? offset : new Vector(0.0, 0.0, 0.0);
        this.data = data;
    }

    public AnimationParticle(Particle particle) {
        this(particle, 1, 0.0, new Vector(0.0, 0.0, 0.0), null);
    }

    public AnimationParticle(Particle particle, int count) {
        this(particle, count, 0.0, new Vector(0.0, 0.0, 0.0), null);
    }

    public AnimationParticle(Particle particle, int count, double extra) {
        this(particle, count, extra, new Vector(0.0, 0.0, 0.0), null);
    }

    public AnimationParticle(Particle particle, int count, double extra, Vector offset) {
        this(particle, count, extra, offset, null);
    }

    public void spawnParticle(Location location, boolean force) {
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

    public void spawnParticle(Location location, List<Player> receivers, Player source, boolean force) {
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

    public void spawnParticle(World world, Vector vector, boolean force) {
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

    public void spawnParticle(World world, Vector vector, List<Player> receivers, Player source, boolean force) {
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
