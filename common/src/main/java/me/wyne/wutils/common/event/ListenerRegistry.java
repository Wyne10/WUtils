package me.wyne.wutils.common.event;

import me.wyne.wutils.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Thin wrapper over Bukkit's own {@link org.bukkit.plugin.PluginManager#registerEvents}, kept
 * only so a group of listeners can be tracked and torn down together.
 *
 * <p>Unlike {@link EventRegistry}, dispatch is handled entirely by Bukkit: this class does not
 * use reflection and listener methods must be {@code public} as usual. Use it when the only
 * thing needed on top of Bukkit's own registration is bulk unregistration via {@link #close()}
 * (e.g. on plugin disable).
 */
public class ListenerRegistry implements Terminable {

    private final Plugin plugin;
    private final Set<Listener> listeners = new HashSet<>();

    public ListenerRegistry(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers {@code listener} with Bukkit and tracks it for later {@link #close()}.
     *
     * <p>Bukkit does not deduplicate registrations: registering the same listener instance more
     * than once causes its {@code @EventHandler} methods to be invoked once per registration.
     */
    public void register(@NotNull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        listeners.add(listener);
    }

    /**
     * Unregisters {@code listener} from Bukkit and stops tracking it.
     */
    public void unregister(@NotNull Listener listener) {
        HandlerList.unregisterAll(listener);
        listeners.remove(listener);
    }

    /**
     * Unregisters every tracked listener from Bukkit.
     */
    @Override
    public void close() {
        listeners.forEach(HandlerList::unregisterAll);
        listeners.clear();
    }

}
