package me.wyne.wutils.animation

import org.bukkit.Bukkit

class BlockingAnimationStep(
    runnable: AnimationRunnable,
    delay: Long = 0,
    period: Long = 0,
    duration: Long = 0
) : BaseAnimationStep(runnable, delay, period, duration) {
    override fun runOnce(animation: Animation) {
        Bukkit.getScheduler().runTaskLater(
            animation.plugin,
            { task ->
                animation.currentTask = task
                runnable.run()
                runnable.close()
                animation.pollStep()?.run(animation)
            },
            delay
        )
    }

    override fun runRepeating(animation: Animation) {
        Bukkit.getScheduler().runTaskTimer(
            animation.plugin,
            { task ->
                if (ticksElapsed <= 0)
                    animation.currentTask = task
                if (duration > 0 && ticksElapsed >= duration) {
                    runnable.close()
                    task.cancel()
                    animation.pollStep()?.run(animation)
                    return@runTaskTimer
                }
                runnable.run()
                ticksElapsed += period
            },
            delay,
            period
        )
    }
}
