package me.wyne.wutils.common.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.*;

public class EventRegistry implements Listener, AutoCloseable {

    private final JavaPlugin plugin;

    private final Map<Class<? extends Event>, Set<RegisterableListener>> registry = new HashMap<>();
    private final Map<RegisterableListener, Map<Class<? extends Event>, Set<Method>>> handlers = new HashMap<>();

    public EventRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(RegisterableListener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(EventHandler.class))
                .forEach(handler -> {
                    var event = handler.getParameterTypes()[0].asSubclass(Event.class);
                    if (!registry.containsKey(event)) {
                        registry.put(event, new HashSet<>());
                        registerEvent(event, handler.getDeclaredAnnotation(EventHandler.class));
                    }
                    registry.get(event).add(listener);
                    if (!handlers.containsKey(listener))
                        handlers.put(listener, new HashMap<>());
                    if (!handlers.get(listener).containsKey(event))
                        handlers.get(listener).put(event, new HashSet<>());
                    handler.setAccessible(true);
                    handlers.get(listener).get(event).add(handler);
                });
    }

    @Override
    public void close() {
        registry.values().forEach(Set::clear);
        handlers.clear();
    }

    private void registerEvent(Class<? extends Event> event, EventHandler parameters) {
        Bukkit.getPluginManager().registerEvent(
                event,
                this,
                parameters.priority(),
                (calledListener, calledEvent) -> {
                    if (event.isInstance(calledEvent)) {
                        registry.get(event).forEach(listener -> {
                            handlers.get(listener).get(event).forEach(method -> {
                                try {
                                    method.invoke(listener, calledEvent);
                                } catch (Exception e) {
                                    plugin.getLogger().severe(e.getMessage());
                                }
                            });
                        });
                    }
                },
                plugin,
                parameters.ignoreCancelled()
        );
    }

}
