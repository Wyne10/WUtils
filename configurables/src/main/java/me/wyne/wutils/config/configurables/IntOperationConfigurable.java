package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.operation.IntOperation;
import me.wyne.wutils.common.operation.Operations;
import me.wyne.wutils.common.operation.Set;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.jetbrains.annotations.Nullable;

public class IntOperationConfigurable implements CompositeConfigurable {

    private IntOperation intOperation = new IntOperation(0, new Set<>());

    public IntOperationConfigurable() {}

    public IntOperationConfigurable(String operation) {
        fromConfig(operation);
    }

    public IntOperationConfigurable(IntOperation intOperation) {
        this.intOperation = intOperation;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return intOperation.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.intOperation = Operations.getIntOperation((String) configObject);
    }

    public IntOperation getIntOperation() {
        return intOperation;
    }

}

