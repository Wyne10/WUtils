package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.range.DoubleRange;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.jetbrains.annotations.Nullable;

public class DoubleRangeConfigurable implements CompositeConfigurable {

    private DoubleRange range = new DoubleRange(0.0, 1.0);

    public DoubleRangeConfigurable() {}

    public DoubleRangeConfigurable(String range) {
        fromConfig(range);
    }

    public DoubleRangeConfigurable(DoubleRange range) {
        this.range = range;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return range.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.range = DoubleRange.getDoubleRange((String) configObject);
    }

    public DoubleRange getRange() {
        return range;
    }

}

