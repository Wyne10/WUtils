package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.range.ClosedIntRange;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.jetbrains.annotations.Nullable;

public class IntRangeConfigurable implements CompositeConfigurable {

    private ClosedIntRange range = new ClosedIntRange(0, 1);

    public IntRangeConfigurable() {}

    public IntRangeConfigurable(String range) {
        fromConfig(range);
    }

    public IntRangeConfigurable(ClosedIntRange range) {
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
        this.range = ClosedIntRange.getIntRange((String) configObject);
    }

    public ClosedIntRange getRange() {
        return range;
    }

}

