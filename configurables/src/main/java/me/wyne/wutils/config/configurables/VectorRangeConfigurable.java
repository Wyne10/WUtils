package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.range.VectorRange;
import me.wyne.wutils.common.vector.VectorUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link VectorRange}, read from a string via {@link VectorRange#getVectorRange}.
 *
 * <p>{@link #fromConfig} casts the incoming value to {@code String} with no {@code instanceof}
 * guard, and a {@code null} config value is a no-op that leaves the previous {@link #getRange()} in
 * place rather than reverting to the constructor default.</p>
 */
public class VectorRangeConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private VectorRange range = new VectorRange(VectorUtils.zero(), 1.0);

    public VectorRangeConfigurable() {}

    public VectorRangeConfigurable(@NotNull String range) {
        fromConfig(range);
    }

    public VectorRangeConfigurable(@NotNull VectorRange range) {
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
        this.range = VectorRange.getVectorRange((String) configObject);
    }

    public @NotNull VectorRange getRange() {
        return range;
    }

}

