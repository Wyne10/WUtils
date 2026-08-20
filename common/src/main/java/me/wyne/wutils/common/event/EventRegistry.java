package me.wyne.wutils.common.event;

import me.wyne.wutils.common.terminable.Terminable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Reflection-based alternative to Bukkit's own {@link org.bukkit.plugin.PluginManager#registerEvents}
 * and to the vendored {@link Events} fluent builder.
 *
 * <p>Where {@code Events} produces one-off, self-contained {@link Subscription} handles, an
 * {@code EventRegistry} is a shared, mutable registration table scoped to a single owner: any
 * number of {@link Listener}s can be {@link #register(Listener) registered} and
 * {@link #unregister(Listener) unregistered} against it, and every {@code @EventHandler} method
 * found on a listener is dispatched through reflection rather than Bukkit's own listener
 * scanning — the method does not need to be {@code public}.
 *
 * <p>The underlying Bukkit registration for a given event class / {@link EventHandler#priority()}
 * / {@link EventHandler#ignoreCancelled()} combination is installed lazily, the first time it is
 * seen, and is only torn down by {@link #close()}. {@link #unregister(Listener)} stops a listener
 * from being dispatched to but leaves that Bukkit hook installed, so a registry that repeatedly
 * registers and unregisters listeners across many distinct event/priority combinations
 * accumulates permanent {@link HandlerList} entries until it is closed.
 */
public class EventRegistry implements Listener, Terminable {

    private final Plugin plugin;

    private final Map<RegisterableEvent, Set<Listener>> registry = new HashMap<>();
    private final Map<Listener, Map<RegisterableEvent, Set<Method>>> handlers = new HashMap<>();

    public EventRegistry(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Scans {@code listener}'s declared methods for {@link EventHandler} and registers each one.
     *
     * <p>Only methods declared directly on the listener's class are considered; inherited handler
     * methods are not picked up.
     */
    public void register(@NotNull Listener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(EventHandler.class))
                .forEach(method -> {
                    var event = method.getParameterTypes()[0].asSubclass(Event.class);
                    register(listener, event, method);
                });
    }

    /**
     * Registers a single {@code @EventHandler} method for dispatch.
     *
     * <p>Does nothing if {@code method} is not annotated with {@link EventHandler}. Installs the
     * underlying Bukkit registration the first time this event class / priority /
     * ignoreCancelled combination is seen; later calls reuse it.
     */
    public void register(@NotNull Listener listener, @NotNull Class<? extends Event> event, @NotNull Method method) {
        if (!method.isAnnotationPresent(EventHandler.class)) return;
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
    }

    /**
     * Unregisters every listener and removes all Bukkit hooks installed by this registry.
     *
     * @throws Exception if reflectively resolving or invoking an event class's
     *                    {@code getHandlerList()} method fails
     */
    @Override
    public void close() throws Exception {
        clear();
        for (RegisterableEvent event : registry.keySet()) {
            Method method = event.event().getMethod("getHandlerList");
            method.setAccessible(true);
            HandlerList handlerList = (HandlerList) method.invoke(null);
            handlerList.unregister(this);
        }
    }

    /**
     * Stops dispatching to {@code listener}.
     *
     * <p>Only removes {@code listener} from the dispatch tables; the underlying Bukkit event
     * registration it relied on stays installed until {@link #close()} is called.
     */
    public void unregister(@NotNull Listener listener) {
        registry.values().forEach(listeners -> listeners.remove(listener));
        handlers.remove(listener);
    }

    /**
     * Stops dispatching to every listener, without removing the underlying Bukkit registrations.
     */
    public void clear() {
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
                            var listenerHandlers = handlers.get(listener);
                            if (listenerHandlers == null) return;
                            var methods = listenerHandlers.get(event);
                            if (methods == null) return;
                            methods.forEach(method -> {
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
