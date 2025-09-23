package me.wyne.wutils.common.event;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.*;

public class EventRegistry implements Listener, AutoCloseable {

    private final JavaPlugin plugin;

    private final Map<RegisterableEvent, Set<RegisterableListener>> registry = new HashMap<>();
    private final Map<RegisterableListener, Map<RegisterableEvent, Set<Method>>> handlers = new HashMap<>();

    public EventRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(RegisterableListener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(EventHandler.class))
                .forEach(method -> {
                    var event = method.getParameterTypes()[0].asSubclass(Event.class);
                    var handler = method.getDeclaredAnnotation(EventHandler.class);
                    var registerableEvent = new RegisterableEvent(event, handler);
                    if (!registry.containsKey(registerableEvent)) {
                        registry.put(registerableEvent, new HashSet<>());
                        registerEvent(registerableEvent);
                    }
                    registry.get(registerableEvent).add(listener);
                    if (!handlers.containsKey(listener))
                        handlers.put(listener, new HashMap<>());
                    if (!handlers.get(listener).containsKey(registerableEvent))
                        handlers.get(listener).put(registerableEvent, new HashSet<>());
                    method.setAccessible(true);
                    handlers.get(listener).get(registerableEvent).add(method);
                    applyEventHandlerTimer(listener, registerableEvent, method);
                });
    }

    private void applyEventHandlerTimer(RegisterableListener listener, RegisterableEvent event, Method method) {
        if (!method.isAnnotationPresent(EventTimer.class)) return;
        var ticks =  method.getDeclaredAnnotation(EventTimer.class).ticks();
        if (ticks <= 0) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> handlers.get(listener).get(event).remove(method), ticks);
    }

    @Override
    public void close() {
        registry.values().forEach(Set::clear);
        handlers.clear();
    }

    private void registerEvent(RegisterableEvent event) {
        Bukkit.getPluginManager().registerEvent(
                event.event(),
                this,
                event.handler().priority(),
                (calledListener, calledEvent) -> {
                    if (event.event().isInstance(calledEvent)) {
                        registry.get(event).forEach(listener -> {
                            handlers.get(listener).get(event).forEach(method -> {
                                try {
                                    method.invoke(listener, calledEvent);
                                } catch (Exception e) {
                                    plugin.getLogger().severe(ExceptionUtils.getStackTrace(e));
                                }
                            });
                        });
                    }
                },
                plugin,
                event.handler().ignoreCancelled()
        );
    }

}
