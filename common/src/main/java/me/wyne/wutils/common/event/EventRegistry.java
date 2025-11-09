package me.wyne.wutils.common.event;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;

public class EventRegistry implements Listener, AutoCloseable {

    private final Plugin plugin;

    private final Map<RegisterableEvent, Set<RegisterableListener>> registry = new HashMap<>();
    private final Map<RegisterableListener, Map<RegisterableEvent, Set<Method>>> handlers = new HashMap<>();

    public EventRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(RegisterableListener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(EventHandler.class))
                .forEach(method -> {
                    var event = method.getParameterTypes()[0].asSubclass(Event.class);
                    register(plugin, listener, event, method);
                });
    }

    public void register(Plugin plugin, RegisterableListener listener, Class<? extends Event> event, Method method) {
        if (!method.isAnnotationPresent(EventHandler.class)) return;
        var handler = method.getDeclaredAnnotation(EventHandler.class);
        var registerableEvent = new RegisterableEvent(event, handler);
        if (!registry.containsKey(registerableEvent)) {
            registry.put(registerableEvent, new HashSet<>());
            registerEvent(plugin, registerableEvent);
        }
        registry.get(registerableEvent).add(listener);
        if (!handlers.containsKey(listener))
            handlers.put(listener, new HashMap<>());
        if (!handlers.get(listener).containsKey(registerableEvent))
            handlers.get(listener).put(registerableEvent, new HashSet<>());
        method.setAccessible(true);
        handlers.get(listener).get(registerableEvent).add(method);
    }

    @Override
    public void close() {
        clear();
        registry.keySet().forEach(event -> {
            try {
                Method method = event.event().getMethod("getHandlerList");
                method.setAccessible(true);
                HandlerList handlerList = (HandlerList) method.invoke(null);
                handlerList.unregister(this);
            } catch (Exception e) {
                throw new RuntimeException("An exception occurred trying to close event registry", e);
            }
        });
    }

    public void clear() {
        registry.values().forEach(Set::clear);
        handlers.clear();
    }

    private void registerEvent(Plugin plugin, RegisterableEvent event) {
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
