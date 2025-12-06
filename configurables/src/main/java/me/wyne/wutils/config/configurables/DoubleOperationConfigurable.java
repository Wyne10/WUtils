package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.operation.DoubleOperation;
import me.wyne.wutils.common.operation.Operations;
import me.wyne.wutils.common.operation.Set;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.jetbrains.annotations.Nullable;

public class DoubleOperationConfigurable implements CompositeConfigurable {

    private DoubleOperation doubleOperation = new DoubleOperation(0, new Set<>());

    public DoubleOperationConfigurable() {}

    public DoubleOperationConfigurable(String operation) {
        fromConfig(operation);
    }

    public DoubleOperationConfigurable(DoubleOperation doubleOperation) {
        this.doubleOperation = doubleOperation;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return doubleOperation.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.doubleOperation = Operations.getDoubleOperation((String) configObject);
    }

    public DoubleOperation getDoubleOperation() {
        return doubleOperation;
    }

}

