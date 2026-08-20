package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.comparator.Comparators;
import me.wyne.wutils.common.comparator.Equals;
import me.wyne.wutils.common.comparator.IntComparator;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link IntComparator}, read from a string via {@link Comparators#getIntComparator}.
 *
 * <p>{@link #fromConfig} casts the incoming value to {@code String} with no {@code instanceof}
 * guard, and a {@code null} config value is a no-op that leaves the previous
 * {@link #getIntComparator()} in place rather than reverting to the constructor default.</p>
 */
public class IntComparatorConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private IntComparator intComparator = new IntComparator(0, new Equals<>());

    public IntComparatorConfigurable() {}

    public IntComparatorConfigurable(@NotNull String comparator) {
        fromConfig(comparator);
    }

    public IntComparatorConfigurable(@NotNull IntComparator intComparator) {
        this.intComparator = intComparator;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return intComparator.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.intComparator = Comparators.getIntComparator((String) configObject);
    }

    public @NotNull IntComparator getIntComparator() {
        return intComparator;
    }

}

