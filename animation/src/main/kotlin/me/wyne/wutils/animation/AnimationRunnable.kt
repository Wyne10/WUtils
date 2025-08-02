package me.wyne.wutils.animation

interface AnimationRunnable : Runnable, AutoCloseable {
    fun run(delay: Long, period: Long, duration: Long) = run()
    override fun run() = Unit
    override fun close() = Unit

    companion object {
        val EMPTY: AnimationRunnable = object : AnimationRunnable {}
        fun runnable(runnable: Runnable) = object : AnimationRunnable {
            override fun run(delay: Long, period: Long, duration: Long) = runnable.run()
        }
    }
}