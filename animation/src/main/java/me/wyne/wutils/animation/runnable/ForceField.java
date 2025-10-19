package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public record ForceField(Location location, double radius, double velocity) implements AnimationRunnable {

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
