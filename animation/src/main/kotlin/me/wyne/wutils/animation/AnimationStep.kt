package me.wyne.wutils.animation

interface AnimationStep : AutoCloseable {
    fun run(animation: Animation)
}

abstract class BaseAnimationStep(
    protected val runnable: AnimationRunnable,
    protected val delay: Long = 0,
    protected val period: Long = 0,
    protected val duration: Long = 0
) : AnimationStep {
    protected var ticksElapsed: Long = 0

    override fun run(animation: Animation) {
        ticksElapsed = 0
        createTask(animation)
    }

    override fun close() {
        runnable.close()
    }

    private fun createTask(animation: Animation) {
        if (period == 0L)
            runOnce(animation)
        else
            runRepeating(animation)
    }

    protected abstract fun runOnce(animation: Animation)
    protected abstract fun runRepeating(animation: Animation)
}