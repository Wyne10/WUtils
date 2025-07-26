package me.wyne.wutils.animation.runnable

import me.wyne.wutils.animation.AnimationRunnable
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector

class WorldParticleEffect(
    private val location: Location,
    private val particle: Particle,
    private val count: Int,
    private val speed: Double,
    private val offset: Vector,
) : AnimationRunnable {

    override fun run() {
        location.world.spawnParticle(particle, location, count,
            offset.x,
            offset.y,
            offset.z,
            speed
        )
    }

}