package me.wyne.wutils.animation

import org.bukkit.Bukkit

class AsyncParallelAnimationStep(
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
                animation.parallelTasks.remove(this)
            },
            delay
        )
        animation.parallelTasks[this] = task
        animation.pollStep()?.run(animation)
    }

    override fun runRepeating(animation: Animation) {
        val task = Bukkit.getScheduler().runTaskTimerAsynchronously(
            animation.plugin,
            Runnable {
                if (duration > 0 && ticksElapsed >= duration) {
                    close()
                    animation.parallelTasks.remove(this)?.cancel()
                    return@Runnable
                }
                runnable.run(delay, period, duration)
                ticksElapsed += period
            },
            delay,
            period
        )
        animation.parallelTasks[this] = task
        animation.pollStep()?.run(animation)
    }
}
