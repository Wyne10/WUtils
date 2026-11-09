package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.range.TimeSpanRange;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link TimeSpanRange}, read from a duration string via {@link TimeSpanRange#getTimeSpanRange}.
 *
 * <p>{@link #fromConfig} casts the incoming value to {@code String} with no {@code instanceof}
 * guard, and a {@code null} config value is a no-op that leaves the previous {@link #getRange()}
 * in place rather than reverting to the constructor default.</p>
 */
public class TimeSpanRangeConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private TimeSpanRange range = TimeSpanRange.getTimeSpanRange("0..0");

    public TimeSpanRangeConfigurable() {}

    public TimeSpanRangeConfigurable(@NotNull String range) {
        fromConfig(range);
    }

    public TimeSpanRangeConfigurable(@NotNull TimeSpanRange range) {
        this.range = range;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return range.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.range = TimeSpanRange.getTimeSpanRange((String) configObject);
    }

    public @NotNull TimeSpanRange getRange() {
        return range;
    }

}

