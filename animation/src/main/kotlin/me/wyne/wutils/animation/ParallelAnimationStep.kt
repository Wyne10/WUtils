package me.wyne.wutils.animation

import org.bukkit.Bukkit

class ParallelAnimationStep(
    runnable: AnimationRunnable,
    delay: Long = 0,
    period: Long = 0,
    duration: Long = 0
) : BaseAnimationStep(runnable, delay, period, duration) {
    override fun runOnce(animation: Animation) {
        Bukkit.getScheduler().runTaskLater(
            animation.plugin,
            { task ->
                animation.parallelTasks.add(task)
                Bukkit.getScheduler().runTask(animation.plugin, Runnable {
                    animation.pollStep()?.run(animation)
                })
                runnable.run(delay, period, duration)
                runnable.close()
                animation.parallelTasks.remove(task)
            },
            delay
        )
    }

    override fun runRepeating(animation: Animation) {
        Bukkit.getScheduler().runTaskTimer(
            animation.plugin,
            { task ->
                if (ticksElapsed <= 0) {
                    animation.parallelTasks.add(task)
                    Bukkit.getScheduler().runTask(animation.plugin, Runnable {
                        animation.pollStep()?.run(animation)
                    })
                }
                if (duration > 0 && ticksElapsed >= duration) {
                    runnable.close()
                    task.cancel()
                    animation.parallelTasks.remove(task)
                    return@runTaskTimer
                }
                runnable.run(delay, period, duration)
                ticksElapsed += period
            },
            delay,
            period
        )
    }
}
