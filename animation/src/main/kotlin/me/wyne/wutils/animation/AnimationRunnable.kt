package me.wyne.wutils.animation

fun interface AnimationRunnable : Runnable, AutoCloseable {
    override fun close() = Unit

    companion object {
        val EMPTY: AnimationRunnable = AnimationRunnable {}
    }
}