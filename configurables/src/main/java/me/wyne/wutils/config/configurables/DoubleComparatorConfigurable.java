package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.comparator.Comparators;
import me.wyne.wutils.common.comparator.Equals;
import me.wyne.wutils.common.comparator.DoubleComparator;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link DoubleComparator}, read from a string via {@link Comparators#getDoubleComparator}.
 *
 * <p>{@link #fromConfig} casts the incoming value to {@code String} with no {@code instanceof}
 * guard, and a {@code null} config value is a no-op that leaves the previous
 * {@link #getDoubleComparator()} in place rather than reverting to the constructor default.</p>
 */
public class DoubleComparatorConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private DoubleComparator doubleComparator = new DoubleComparator(0, new Equals<>());

    public DoubleComparatorConfigurable() {}

    public DoubleComparatorConfigurable(@NotNull String comparator) {
        fromConfig(comparator);
    }

    public DoubleComparatorConfigurable(@NotNull DoubleComparator doubleComparator) {
        this.doubleComparator = doubleComparator;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return doubleComparator.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.doubleComparator = Comparators.getDoubleComparator((String) configObject);
    }

    public @NotNull DoubleComparator getDoubleComparator() {
        return doubleComparator;
    }

}

