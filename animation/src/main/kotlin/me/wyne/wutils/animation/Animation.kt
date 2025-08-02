package me.wyne.wutils.animation

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.LinkedList
import java.util.Queue

class Animation(val plugin: JavaPlugin) {

    private val steps: Queue<AnimationStep> = LinkedList()

    private val runSteps: Queue<AnimationStep> = LinkedList()
    val parallelTasks: MutableMap<AnimationStep, BukkitTask> = LinkedHashMap()
    var currentTask: Pair<AnimationStep, BukkitTask>? = null

    fun run() {
        runSteps.addAll(steps)
        runSteps.add(BlockingAnimationStep(AnimationRunnable.runnable { stop() }))
        pollStep()?.run(this)
    }

    fun stop() {
        currentTask?.second?.cancel() // Cancel current blocking task
        currentTask?.first?.close() // Finalize blocking task, since it may not have been finished
        parallelTasks.forEach { it.value.cancel(); it.key.close() } // Cancel and finalize parallel tasks
        parallelTasks.clear()
        runSteps.clear() // Run steps are not finalized since they were not started
    }

    fun addStep(step: AnimationStep) =
        steps.add(step)

    fun addSteps(vararg steps: AnimationStep) =
        this.steps.addAll(steps)

    fun pollStep(): AnimationStep? =
        runSteps.poll()

}