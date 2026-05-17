package me.wyne.wutils.common.event;

import me.wyne.wutils.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class ListenerRegistry implements Terminable {

    private final Plugin plugin;
    private final Set<Listener> listeners = new HashSet<>();

    public ListenerRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        listeners.add(listener);
    }

    public void unregister(Listener listener) {
        HandlerList.unregisterAll(listener);
        listeners.remove(listener);
    }

    @Override
    public void close() {
        listeners.forEach(HandlerList::unregisterAll);
        listeners.clear();
    }

}
