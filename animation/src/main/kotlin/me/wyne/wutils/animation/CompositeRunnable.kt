package me.wyne.wutils.animation

class CompositeRunnable(
    private val runnables: List<AnimationRunnable>,
) : AnimationRunnable {
    override fun run(delay: Long, period: Long, duration: Long) {
        runnables.forEach { it.run(delay, period, duration) }
    }

    override fun close() {
        runnables.forEach { it.close() }
    }
}