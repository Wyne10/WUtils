package me.wyne.wutils.animation;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Orchestrates a queue of {@link AnimationStep}s, running them one after another while
 * letting parallel steps run alongside whichever blocking step is currently active.
 *
 * <p>Steps are queued with {@link #addStep}/{@link #addSteps} before {@link #run()} is
 * called. {@link #run()} copies the queue and appends a trailing {@link BlockingAnimationStep}
 * that calls {@link #stop()}, so the animation cleans itself up once every other step has
 * finished without the caller having to track completion.</p>
 *
 * <p>{@link #stop()} (also reachable via {@link #close()}) cancels the in-flight blocking task
 * and any running parallel tasks, calling {@link AnimationStep#close() close()} on each since
 * they had started; it then calls {@link AnimationStep#_finalize() _finalize()} on every step
 * still left in the queue, since those never started and so were never closed.</p>
 */
public class Animation implements AutoCloseable {

    private final Plugin plugin;

    private final Queue<AnimationStep> steps = new ConcurrentLinkedQueue<>();

    private final Queue<AnimationStep> runSteps = new ConcurrentLinkedQueue<>();
    private final Map<AnimationStep, BukkitTask> parallelTasks = new ConcurrentHashMap<>();
    private Pair<AnimationStep, BukkitTask> currentTask = null;

    public Animation(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts the animation, running its queued steps in order.
     */
    public void run() {
        runSteps.addAll(steps);
        runSteps.add(new BlockingAnimationStep(this::stop));
        var step = pollStep();
        if (step != null)
            step.run(this);
    }

    /**
     * Cancels the currently running blocking and parallel tasks, closing their steps, then
     * finalizes and clears every step still left in the queue.
     */
    public void stop() {
        if (currentTask != null) {
            currentTask.getValue1().cancel(); // Cancel current blocking task
            currentTask.getValue0().close(); // Close blocking task, since it may not have been finished
        }
        parallelTasks.forEach((key, value) -> {
            value.cancel();
            key.close();
        }); // Cancel and close parallel tasks
        parallelTasks.clear();
        runSteps.forEach(AnimationStep::_finalize);
        runSteps.clear(); // Run steps are not closed since they were not started
    }

    @Override
    public void close() {
        stop();
    }

    public void addStep(@NotNull AnimationStep step) {
        steps.add(step);
    }

    public void addSteps(@NotNull AnimationStep... steps) {
        for (AnimationStep step : steps) {
            addStep(step);
        }
    }

    /**
     * Queues every step from another animation onto this one; the source animation is left
     * untouched.
     */
    public void addAnimation(@NotNull Animation animation) {
        for (AnimationStep step : animation.steps) {
            addStep(step);
        }
    }

    /**
     * Removes and returns the next queued step, or {@code null} if none remain. Used by step
     * implementations to hand off to the next step in the queue.
     */
    @Nullable
    public AnimationStep pollStep() {
        return runSteps.poll();
    }

    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the live, mutable map of currently running parallel steps to their scheduled
     * Bukkit tasks.
     */
    public @NotNull Map<@NotNull AnimationStep, @NotNull BukkitTask> getParallelTasks() {
        return parallelTasks;
    }

    /**
     * Returns the currently running blocking step and its scheduled task, or {@code null}
     * before any blocking step has started.
     */
    public @Nullable Pair<@NotNull AnimationStep, @NotNull BukkitTask> getCurrentTask() {
        return currentTask;
    }

    /**
     * Records the task backing the currently running blocking step. Called by step
     * implementations as they schedule their Bukkit tasks; synchronized since blocking steps
     * may schedule from an async thread.
     */
    public synchronized void setCurrentTask(@NotNull Pair<@NotNull AnimationStep, @NotNull BukkitTask> currentTask) {
        this.currentTask = currentTask;
    }

}
