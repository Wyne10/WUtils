package me.wyne.wutils.animation.prefab

import me.wyne.wutils.animation.AnimationRunnable
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector

class WorldParticleEffect(
    private val origin: Location,
    private val count: Int,
    private val offset: Vector,
    private val speed: Double,
    private val particle: Particle,
) : AnimationRunnable {

    override fun run() {
        origin.world.spawnParticle(particle, origin, count,
            offset.x,
            offset.y,
            offset.z,
            speed
        )
    }

}