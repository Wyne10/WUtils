package me.wyne.wutils.animation.prefab

import me.wyne.wutils.animation.AnimationRunnable
import org.bukkit.Location
import org.bukkit.Sound

class WorldSoundEffect(
    private val origin: Location,
    private val sound: Sound,
    private val volume: Float = 1.0f,
    private val pitch: Float = 1.0f,
) : AnimationRunnable {

    override fun run() {
        origin.world.playSound(origin, sound, volume, pitch)
    }

}