package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Pushes every player within {@code radius} of {@code location} away from it at
 * {@code velocity}. Does nothing if {@code location}'s world is not currently loaded.
 */
public record ForceField(@NotNull Location location, double radius, double velocity) implements AnimationRunnable {

    @Override
    public void run() {
        World world = location.getWorld();
        if (world == null) return;

        Collection<Player> nearbyPlayers = world.getNearbyPlayers(location, radius);
        for (Player player : nearbyPlayers) {
            Vector direction = player.getLocation().toVector().subtract(location.toVector()).multiply(velocity);
            player.setVelocity(direction);
        }
    }

}
