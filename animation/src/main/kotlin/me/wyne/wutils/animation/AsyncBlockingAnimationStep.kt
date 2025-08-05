package me.wyne.wutils.animation

import org.bukkit.Bukkit

class AsyncBlockingAnimationStep(
    runnable: AnimationRunnable,
    delay: Long = 0,
    period: Long = 0,
    duration: Long = 0
) : BaseAnimationStep(runnable, delay, period, duration) {
    override fun runOnce(animation: Animation) {
        val task = Bukkit.getScheduler().runTaskLaterAsynchronously(
            animation.plugin,
            Runnable {
                runnable.run(delay, period, duration)
                close()
                animation.pollStep()?.run(animation)
            },
            delay
        )
        animation.currentTask = this to task
    }

    override fun runRepeating(animation: Animation) {
        val task = Bukkit.getScheduler().runTaskTimerAsynchronously(
            animation.plugin,
            Runnable {
                if (duration > 0 && ticksElapsed >= duration) {
                    close()
                    animation.currentTask?.second?.cancel()
                    animation.pollStep()?.run(animation)
                    return@Runnable
                }
                runnable.run(delay, period, duration)
                ticksElapsed += period
            },
            delay,
            period
        )
        animation.currentTask = this to task
    }
}
