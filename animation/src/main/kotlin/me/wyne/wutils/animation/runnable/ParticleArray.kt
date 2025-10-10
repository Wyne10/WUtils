package me.wyne.wutils.animation.runnable

import me.wyne.wutils.animation.AnimationRunnable
import me.wyne.wutils.animation.data.AnimationParticle
import org.bukkit.World
import org.bukkit.util.Vector

class ParticleArray(
    private val world: World,
    private val points: Set<Vector>,
    private val particle: AnimationParticle,
) : AnimationRunnable {

    override fun run() {
        points.forEach { particle.spawnParticle(world, it) }
    }

}