package me.wyne.wutils.common.scheduler;

import me.wyne.wutils.common.event.EventRegistry;
import me.wyne.wutils.common.event.RegisterableListener;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ExtendedScheduler implements RegisterableListener {

    public static ExtendedScheduler global = new ExtendedScheduler();

    private final EventRegistry eventRegistry = new EventRegistry(null);

    private final Map<Class<? extends Event>, Set<PromisedTask<?>>> promisedTasks = new HashMap<>();
    private Method promiseMethod = null;

    public ExtendedScheduler() {
        try {
            promiseMethod = getClass().getDeclaredMethod("onEvent", Event.class);
        } catch (NoSuchMethodException ignored) {}
    }

    public <T extends Event> void runTaskLaterPromised(Plugin plugin, Runnable runnable, Consumer<T> promise, Predicate<T> condition, long delay, Class<T> event) {
        if (!promisedTasks.containsKey(event))
            promisedTasks.put(event, new LinkedHashSet<>());
        var task = new PromisedTask<>(plugin, runnable, promise, condition, delay);
        promisedTasks.get(event).add(task);
        eventRegistry.register(task.getPlugin(), this, event, promiseMethod);
        task.start();
    }

    public <T extends Event> void runTaskLaterPromised(Plugin plugin, Runnable runnable, Consumer<T> promise, long delay, Class<T> event) {
        runTaskLaterPromised(plugin, runnable, promise, event1 -> true, delay, event);
    }

    public <T extends Event> void runTaskLaterPromised(Plugin plugin, Runnable runnable, Predicate<T> condition, long delay, Class<T> event) {
        runTaskLaterPromised(plugin, runnable, event1 -> runnable.run(), condition, delay, event);
    }

    public void runTaskLaterPromised(Plugin plugin, Runnable runnable, long delay, Class<? extends Event>... events) {
        for (Class<? extends Event> event : events) {
            runTaskLaterPromised(plugin, runnable, event1 -> runnable.run(), event1 -> true, delay, event);
        }
    }

    @EventHandler
    private void onEvent(Event event) {
        promisedTasks.remove(event.getClass()).forEach(task -> task.cancel(event));
    }

}
