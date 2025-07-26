package me.wyne.wutils.animation

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.LinkedList
import java.util.Queue

class Animation(val plugin: JavaPlugin) {

    private val steps: Queue<AnimationStep> = LinkedList()

    private val runSteps: Queue<AnimationStep> = LinkedList()
    val parallelTasks: MutableList<BukkitTask> = LinkedList()
    var currentTask: BukkitTask? = null

    fun run() {
        runSteps.addAll(steps)
        runSteps.add(BlockingAnimationStep({ stop() }))
        pollStep()?.run(this)
    }

    fun stop() {
        currentTask?.cancel()
        parallelTasks.forEach { it.cancel() }
        parallelTasks.clear()
        runSteps.forEach { it.close() }
        runSteps.clear()
    }

    fun addStep(step: AnimationStep) =
        steps.add(step)

    fun addSteps(vararg steps: AnimationStep) =
        this.steps.addAll(steps)

    fun pollStep(): AnimationStep? =
        runSteps.poll()

}