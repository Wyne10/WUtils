package me.wyne.wutils.animation.runnable

import me.wyne.wutils.animation.AnimationRunnable
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.title.Title

class TitleEffect(
    private val audience: Audience,
    private val title: Title
) : AnimationRunnable {

    override fun run() {
        audience.showTitle(title)
    }

}