package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.range.DoubleRange;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link DoubleRange}, read from a string via {@link DoubleRange#getDoubleRange}.
 *
 * <p>{@link #fromConfig} casts the incoming value to {@code String} with no {@code instanceof}
 * guard, and a {@code null} config value is a no-op that leaves the previous {@link #getRange()} in
 * place rather than reverting to the constructor default.</p>
 */
public class DoubleRangeConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private DoubleRange range = new DoubleRange(0.0, 1.0);

    public DoubleRangeConfigurable() {}

    public DoubleRangeConfigurable(@NotNull String range) {
        fromConfig(range);
    }

    public DoubleRangeConfigurable(@NotNull DoubleRange range) {
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
        this.range = DoubleRange.getDoubleRange((String) configObject);
    }

    public @NotNull DoubleRange getRange() {
        return range;
    }

}

