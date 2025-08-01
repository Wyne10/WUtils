package me.wyne.wutils.animation.runnable

import me.wyne.wutils.animation.AnimationRunnable
import org.bukkit.Location
import org.bukkit.Sound

class LocalSound(
    private val location: Location,
    private val sound: Sound,
    private val volume: Float = 1.0f,
    private val pitch: Float = 1.0f,
) : AnimationRunnable {

    override fun run() {
        location.world.playSound(location, sound, volume, pitch)
    }

}