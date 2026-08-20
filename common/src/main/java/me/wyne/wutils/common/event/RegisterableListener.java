package me.wyne.wutils.common.event;

import org.bukkit.event.Listener;

/**
 * Marker interface for {@link Listener} implementations meant to be registered through
 * {@link EventRegistry}'s reflection-based dispatch rather than Bukkit's own
 * {@link org.bukkit.plugin.PluginManager#registerEvents}.
 *
 * <p>Implement it on listener types built specifically around {@code EventRegistry}, such as
 * {@link me.wyne.wutils.common.scheduler.EventPromisedTask}.
 */
public interface RegisterableListener extends Listener {}
