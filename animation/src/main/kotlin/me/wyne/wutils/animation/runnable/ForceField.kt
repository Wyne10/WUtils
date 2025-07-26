package me.wyne.wutils.animation.runnable

import me.wyne.wutils.animation.AnimationRunnable
import org.bukkit.Location
import kotlin.collections.forEach

class ForceField(
    private val location: Location,
    private val radius: Double,
    private val velocity: Double,
) : AnimationRunnable {

    override fun run() {
        location.world.getNearbyPlayers(location, radius)
            .forEach { it.velocity = it.location.toVector().subtract(location.toVector()).multiply(velocity) }
    }

}