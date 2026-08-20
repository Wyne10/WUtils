package me.wyne.wutils.animation.runnable;

import me.wyne.wutils.animation.AnimationRunnable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Spawns a firework at {@code location} with the given meta. Does nothing if
 * {@code location}'s world is not currently loaded.
 */
public record Firework(@NotNull Location location, @NotNull FireworkMeta fireworkMeta) implements AnimationRunnable {

    @Override
    public void run() {
        World world = location.getWorld();
        if (world == null) return;

        org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) world.spawnEntity(location, EntityType.FIREWORK);
        firework.setFireworkMeta(fireworkMeta);
    }

}
