package me.wyne.wutils.animation;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Animation {

    private final Plugin plugin;

    private final Queue<AnimationStep> steps = new LinkedList<>();

    private final Queue<AnimationStep> runSteps = new LinkedList<>();
    private final Map<AnimationStep, BukkitTask> parallelTasks = new LinkedHashMap<>();
    private Pair<AnimationStep, BukkitTask> currentTask = null;

    public Animation(Plugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        runSteps.addAll(steps);
        runSteps.add(new BlockingAnimationStep(AnimationRunnable.runnable(this::stop)));
        var step = pollStep();
        if (step != null)
            step.run(this);
    }

    public void stop() {
        if (currentTask != null) {
            currentTask.getValue1().cancel(); // Cancel current blocking task
            currentTask.getValue0().close(); // Finalize blocking task, since it may not have been finished
        }
        parallelTasks.entrySet().forEach(entry -> { entry.getValue().cancel(); entry.getKey().close(); }); // Cancel and finalize parallel tasks
        parallelTasks.clear();
        runSteps.clear(); // Run steps are not finalized since they were not started
    }

    public void addStep(AnimationStep step) {
        steps.add(step);
    }

    public void addSteps(AnimationStep... steps) {
        for (AnimationStep step : steps) {
            addStep(step);
        }
    }

    public void addAnimation(Animation animation) {
        for (AnimationStep step : animation.steps) {
            addStep(step);
        }
    }

    @Nullable
    public AnimationStep pollStep() {
        return runSteps.poll();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Map<AnimationStep, BukkitTask> getParallelTasks() {
        return parallelTasks;
    }

    public Pair<AnimationStep, BukkitTask> getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Pair<AnimationStep, BukkitTask> currentTask) {
        this.currentTask = currentTask;
    }

}
