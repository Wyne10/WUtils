package me.wyne.wutils.animation

import org.bukkit.Bukkit

class AsyncBlockingAnimationStep(
    runnable: AnimationRunnable,
    delay: Long = 0,
    period: Long = 0,
    duration: Long = 0
) : BaseAnimationStep(runnable, delay, period, duration) {
    override fun runOnce(animation: Animation) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(
            animation.plugin,
            { task ->
                animation.currentTask = this to task
                runnable.run(delay, period, duration)
                close()
                animation.pollStep()?.run(animation)
            },
            delay
        )
    }

    override fun runRepeating(animation: Animation) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            animation.plugin,
            { task ->
                if (ticksElapsed <= 0)
                    animation.currentTask = this to task
                if (duration > 0 && ticksElapsed >= duration) {
                    close()
                    task.cancel()
                    animation.pollStep()?.run(animation)
                    return@runTaskTimerAsynchronously
                }
                runnable.run(delay, period, duration)
                ticksElapsed += period
            },
            delay,
            period
        )
    }
}
