package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class TimeSpanConfigurable implements CompositeConfigurable {

    private TimeSpan timeSpan = new TimeSpan(0, Durations.Ticks);

    public TimeSpanConfigurable() {}

    public TimeSpanConfigurable(String duration) {
        fromConfig(duration);
    }

    public TimeSpanConfigurable(TimeSpan timeSpan) {
        this.timeSpan = timeSpan;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return timeSpan.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.timeSpan = Durations.getTimeSpan((String) configObject);
    }

    public TimeSpan getTimeSpan() {
        return timeSpan;
    }

}

