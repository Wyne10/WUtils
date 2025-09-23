package me.wyne.wutils.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

public record RegisterableEvent(Class<? extends Event> event, EventHandler handler) {
    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof RegisterableEvent other)) return false;

        if (!event.equals(other.event)) return false;
        if (handler.priority() != other.handler.priority()) return false;
        if (handler.ignoreCancelled() != other.handler.ignoreCancelled()) return false;
        return true;
    }
}

