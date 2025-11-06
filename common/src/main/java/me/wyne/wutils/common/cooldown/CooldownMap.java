package me.wyne.wutils.common.cooldown;

import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownMap<T> {

    private final Map<T, Long> cooldownMap;

    public CooldownMap() {
        cooldownMap = new HashMap<>();
    }

    public CooldownMap(boolean concurrent) {
        cooldownMap = concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    public Map<T, Long> getMap() {
        return cooldownMap;
    }

    public boolean isCooldowned(T key) {
        return cooldownMap.containsKey(key) && cooldownMap.get(key) > System.currentTimeMillis();
    }

    public void put(T key, long durationMillis) {
        cooldownMap.put(key, System.currentTimeMillis() + durationMillis);
    }

    public void put(T key, long duration, TimeUnit unit) {
        cooldownMap.put(key, System.currentTimeMillis() + unit.toMillis(duration));
    }

    public void put(T key, TimeSpan duration) {
        put(key, duration.getMillis());
    }

    public void remove(T key) {
        cooldownMap.remove(key);
    }

    public Period getAsPeriod(T key) {
        return new Period(cooldownMap.getOrDefault(key, 0L));
    }

    public long getRemaining(T key) {
        if (!isCooldowned(key))
            return 0;

        return cooldownMap.get(key) - System.currentTimeMillis();
    }

    public long getRemaining(T key, TimeUnit unit) {
        return unit.convert(getRemaining(key), TimeUnit.MILLISECONDS);
    }

    public TimeSpan getRemainingDuration(T key) {
        return new TimeSpan(getRemaining(key), Durations.Millis);
    }

    public String getRemainingStringFormat(T key) {
        return DurationFormatUtils
                .formatDurationHMS(getRemaining(key));
    }

    public String getRemainingStringFormat(T key, String format) {
        return DurationFormatUtils
                .formatDuration(getRemaining(key), format);
    }

}
