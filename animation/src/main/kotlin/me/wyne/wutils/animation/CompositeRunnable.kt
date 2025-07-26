package me.wyne.wutils.animation

class CompositeRunnable(
    private val runnables: List<AnimationRunnable>,
) : AnimationRunnable {
    override fun run() {
        runnables.forEach { it.run() }
    }

    override fun close() {
        runnables.forEach { it.close() }
    }
}