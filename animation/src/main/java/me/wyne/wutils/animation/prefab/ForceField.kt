package me.wyne.wutils.animation.prefab

import me.wyne.wutils.animation.AnimationRunnable
import org.bukkit.Location
import kotlin.collections.forEach

class ForceField(
    private val origin: Location,
    private val radius: Double,
    private val velocity: Double,
) : AnimationRunnable {

    override fun run() {
        origin.world.getNearbyPlayers(origin, radius)
            .forEach { it.velocity = it.location.toVector().subtract(origin.toVector()).multiply(velocity) }
    }

}