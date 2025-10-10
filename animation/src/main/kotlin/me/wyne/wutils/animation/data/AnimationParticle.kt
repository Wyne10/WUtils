package me.wyne.wutils.animation.data

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class AnimationParticle(
    private val particle: Particle,
    private val count: Int = 1,
    private val extra: Double = 0.0,
    private val offset: Vector = Vector(0.0, 0.0, 0.0),
    private val data: Any? = null
) {

    fun spawnParticle(location: Location, force: Boolean = false) =
        location.world.spawnParticle(particle, location, count, offset.x, offset.y, offset.z, extra, data, force)

    fun spawnParticle(location: Location, receivers: List<Player>?, source: Player, force: Boolean = false) =
        location.world.spawnParticle(particle, receivers, source, location.x, location.y, location.z, count, offset.x, offset.y, offset.z, extra, data, force)

    fun spawnParticle(world: World, vector: Vector, force: Boolean = false) =
        world.spawnParticle(particle, vector.x, vector.y, vector.z, count, offset.x, offset.y, offset.z, extra, data, force)

    fun spawnParticle(world: World, vector: Vector, receivers: List<Player>?, source: Player, force: Boolean = false) =
        world.spawnParticle(particle, receivers, source, vector.x, vector.y, vector.z, count, offset.x, offset.y, offset.z, extra, data, force)


}