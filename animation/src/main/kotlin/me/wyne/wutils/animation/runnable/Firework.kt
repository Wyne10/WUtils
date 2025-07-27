package me.wyne.wutils.animation.runnable

import me.wyne.wutils.animation.AnimationRunnable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta

class Firework(
    private val location: Location,
    private val fireworkMeta: FireworkMeta
) : AnimationRunnable {

    override fun run() {
        val firework = location.world.spawnEntity(location, EntityType.FIREWORK) as Firework
        firework.fireworkMeta = fireworkMeta
    }

}

fun createFireworkMeta(): FireworkMeta =
    ItemStack(Material.FIREWORK_ROCKET).itemMeta as FireworkMeta