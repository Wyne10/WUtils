package me.wyne.wutils.common.scheduler;

import me.wyne.wutils.common.event.EventRegistry;
import me.wyne.wutils.common.event.RegisterableListener;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.*;

public class ExtendedScheduler implements RegisterableListener {

    public static ExtendedScheduler global = new ExtendedScheduler();

    private final EventRegistry eventRegistry = new EventRegistry(null);

    private final Map<Class<? extends Event>, Set<PromisedTask>> promisedTasks = new HashMap<>();
    private Method promiseMethod = null;

    public ExtendedScheduler() {
        try {
            promiseMethod = getClass().getDeclaredMethod("onEvent", Event.class);
        } catch (NoSuchMethodException ignored) {}
    }

    public void runTaskLaterPromised(JavaPlugin plugin, Runnable runnable, long delay, Class<? extends Event>... events) {
        for (Class<? extends Event> event : events) {
            if (!promisedTasks.containsKey(event))
                promisedTasks.put(event, new LinkedHashSet<>());
            var task = new PromisedTask(plugin, runnable, delay);
            promisedTasks.get(event).add(task);
            eventRegistry.register(task.getPlugin(), this, event, promiseMethod);
            task.start();
        }
    }

    @EventHandler
    private void onEvent(Event event) {
        promisedTasks.remove(event.getClass()).forEach(PromisedTask::cancel);
    }

}
