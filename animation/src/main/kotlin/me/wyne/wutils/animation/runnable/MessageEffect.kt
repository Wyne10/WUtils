package me.wyne.wutils.animation.runnable

import me.wyne.wutils.animation.AnimationRunnable
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

class MessageEffect(
    private val audience: Audience,
    private val message: Component
) : AnimationRunnable {

    override fun run() {
        audience.sendMessage(message)
    }

}