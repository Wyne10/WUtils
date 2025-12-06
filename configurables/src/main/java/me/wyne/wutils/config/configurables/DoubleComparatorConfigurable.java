package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.comparator.Comparators;
import me.wyne.wutils.common.comparator.Equals;
import me.wyne.wutils.common.comparator.DoubleComparator;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.jetbrains.annotations.Nullable;

public class DoubleComparatorConfigurable implements CompositeConfigurable {

    private DoubleComparator doubleComparator = new DoubleComparator(0, new Equals<>());

    public DoubleComparatorConfigurable() {}

    public DoubleComparatorConfigurable(String comparator) {
        fromConfig(comparator);
    }

    public DoubleComparatorConfigurable(DoubleComparator doubleComparator) {
        this.doubleComparator = doubleComparator;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return doubleComparator.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.doubleComparator = Comparators.getDoubleComparator((String) configObject);
    }

    public DoubleComparator getDoubleComparator() {
        return doubleComparator;
    }

}

