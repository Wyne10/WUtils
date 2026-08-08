package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.comparator.Comparators;
import me.wyne.wutils.common.comparator.Equals;
import me.wyne.wutils.common.comparator.IntComparator;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.Nullable;

public class IntComparatorConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private IntComparator intComparator = new IntComparator(0, new Equals<>());

    public IntComparatorConfigurable() {}

    public IntComparatorConfigurable(String comparator) {
        fromConfig(comparator);
    }

    public IntComparatorConfigurable(IntComparator intComparator) {
        this.intComparator = intComparator;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return intComparator.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.intComparator = Comparators.getIntComparator((String) configObject);
    }

    public IntComparator getIntComparator() {
        return intComparator;
    }

}

