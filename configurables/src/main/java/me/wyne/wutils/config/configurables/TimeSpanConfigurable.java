package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link TimeSpan}, read from a duration string via {@link Durations#getTimeSpan}.
 *
 * <p>{@link #fromConfig} casts the incoming value to {@code String} with no {@code instanceof}
 * guard, and a {@code null} config value is a no-op that leaves the previous {@link #getTimeSpan()}
 * in place rather than reverting to the constructor default.</p>
 */
public class TimeSpanConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private TimeSpan timeSpan = new TimeSpan(0, Durations.Ticks);

    public TimeSpanConfigurable() {}

    public TimeSpanConfigurable(@NotNull String duration) {
        fromConfig(duration);
    }

    public TimeSpanConfigurable(@NotNull TimeSpan timeSpan) {
        this.timeSpan = timeSpan;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return timeSpan.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.timeSpan = Durations.getTimeSpan((String) configObject);
    }

    public @NotNull TimeSpan getTimeSpan() {
        return timeSpan;
    }

}

