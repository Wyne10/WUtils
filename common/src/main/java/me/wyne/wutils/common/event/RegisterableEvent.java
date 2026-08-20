package me.wyne.wutils.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Identifies a single Bukkit event registration by its event type and handler configuration.
 *
 * <p>Used by {@link EventRegistry} as a map key so that every listener method sharing the same
 * event class, {@link EventHandler#priority()} and {@link EventHandler#ignoreCancelled()} can be
 * dispatched through one underlying Bukkit registration.
 */
public record RegisterableEvent(@NotNull Class<? extends Event> event, @NotNull EventHandler handler) {
    /**
     * Compares by {@link #event()} and by the {@link #handler()}'s {@code priority} and
     * {@code ignoreCancelled}, ignoring any other state of the annotation instance.
     */
    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof RegisterableEvent other)) return false;

        if (!event.equals(other.event)) return false;
        if (handler.priority() != other.handler.priority()) return false;
        if (handler.ignoreCancelled() != other.handler.ignoreCancelled()) return false;
        return true;
    }
}

