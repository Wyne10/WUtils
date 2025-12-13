package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.range.VectorRange;
import me.wyne.wutils.common.vector.VectorUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.jetbrains.annotations.Nullable;

public class VectorRangeConfigurable implements CompositeConfigurable {

    private VectorRange range = new VectorRange(VectorUtils.zero(), 1.0);

    public VectorRangeConfigurable() {}

    public VectorRangeConfigurable(String range) {
        fromConfig(range);
    }

    public VectorRangeConfigurable(VectorRange range) {
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
        this.range = VectorRange.getVectorRange((String) configObject);
    }

    public VectorRange getRange() {
        return range;
    }

}

