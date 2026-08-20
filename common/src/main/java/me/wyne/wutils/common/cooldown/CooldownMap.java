package me.wyne.wutils.common.cooldown;

import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Tracks a per-key cooldown expiry timestamp, e.g. one cooldown per player UUID.
 * <p>
 * Keys are held indefinitely once added: an expired entry is simply treated as "not
 * cooldowned" by {@link #isCooldowned}, it is not evicted from the backing map until
 * {@link #remove} is called explicitly. Reads such as {@link #getAsPeriod} never
 * insert an entry for a key that isn't already present.
 *
 * @param <T> the key type, e.g. a player UUID; must have proper {@code equals}/{@code hashCode}
 */
public class CooldownMap<T> {

    private final Map<T, Long> cooldownMap;

    /** Backs the map with a plain {@link HashMap} (not thread-safe). */
    public CooldownMap() {
        cooldownMap = new HashMap<>();
    }

    /** Backs the map with a {@link ConcurrentHashMap} when {@code concurrent} is {@code true}. */
    public CooldownMap(boolean concurrent) {
        cooldownMap = concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    /**
     * Returns the backing map directly (not a copy); mutating it mutates this
     * {@code CooldownMap}. Values are absolute expiry timestamps in epoch milliseconds.
     */
    public @NotNull Map<@NotNull T, @NotNull Long> getMap() {
        return cooldownMap;
    }

    /** Whether {@code key} is present and its expiry is still in the future. */
    public boolean isCooldowned(@NotNull T key) {
        return cooldownMap.containsKey(key) && cooldownMap.get(key) > System.currentTimeMillis();
    }

    /** Starts (or restarts) the cooldown for {@code key}, expiring after {@code durationMillis}. */
    public void put(@NotNull T key, long durationMillis) {
        cooldownMap.put(key, System.currentTimeMillis() + durationMillis);
    }

    public void put(@NotNull T key, long duration, @NotNull TimeUnit unit) {
        cooldownMap.put(key, System.currentTimeMillis() + unit.toMillis(duration));
    }

    public void put(@NotNull T key, @NotNull TimeSpan duration) {
        put(key, duration.getMillis());
    }

    /** Removes {@code key}'s entry; the only way an entry leaves the map. */
    public void remove(@NotNull T key) {
        cooldownMap.remove(key);
    }

    /** Wraps {@code key}'s expiry as a {@link Period}; {@code 0} (already expired) if absent. */
    public @NotNull Period getAsPeriod(@NotNull T key) {
        return new Period(cooldownMap.getOrDefault(key, 0L));
    }

    /** Milliseconds until {@code key}'s cooldown ends, or {@code 0} if not cooldowned. */
    public long getRemaining(@NotNull T key) {
        if (!isCooldowned(key))
            return 0;

        return cooldownMap.get(key) - System.currentTimeMillis();
    }

    public long getRemaining(@NotNull T key, @NotNull TimeUnit unit) {
        return unit.convert(getRemaining(key), TimeUnit.MILLISECONDS);
    }

    public @NotNull TimeSpan getRemainingDuration(@NotNull T key) {
        return new TimeSpan(getRemaining(key), Durations.Millis);
    }

    public @NotNull String getRemainingStringFormat(@NotNull T key) {
        return DurationFormatUtils
                .formatDurationHMS(getRemaining(key));
    }

    public @NotNull String getRemainingStringFormat(@NotNull T key, @NotNull String format) {
        return DurationFormatUtils
                .formatDuration(getRemaining(key), format);
    }

}
