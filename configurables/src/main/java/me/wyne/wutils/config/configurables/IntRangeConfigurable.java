package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.range.ClosedIntRange;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link ClosedIntRange}, read from a string via {@link ClosedIntRange#getIntRange}.
 *
 * <p>{@link #fromConfig} casts the incoming value to {@code String} with no {@code instanceof}
 * guard, and a {@code null} config value is a no-op that leaves the previous {@link #getRange()} in
 * place rather than reverting to the constructor default.</p>
 */
public class IntRangeConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private ClosedIntRange range = new ClosedIntRange(0, 1);

    public IntRangeConfigurable() {}

    public IntRangeConfigurable(@NotNull String range) {
        fromConfig(range);
    }

    public IntRangeConfigurable(@NotNull ClosedIntRange range) {
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
        this.range = ClosedIntRange.getIntRange((String) configObject);
    }

    public @NotNull ClosedIntRange getRange() {
        return range;
    }

}

