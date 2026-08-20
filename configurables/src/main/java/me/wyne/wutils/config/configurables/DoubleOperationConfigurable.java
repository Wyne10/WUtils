package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.operation.DoubleOperation;
import me.wyne.wutils.common.operation.Operations;
import me.wyne.wutils.common.operation.Set;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link DoubleOperation}, read from a string via {@link Operations#getDoubleOperation}.
 *
 * <p>{@link #fromConfig} casts the incoming value to {@code String} with no {@code instanceof}
 * guard, and a {@code null} config value is a no-op that leaves the previous
 * {@link #getDoubleOperation()} in place rather than reverting to the constructor default.</p>
 */
public class DoubleOperationConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private DoubleOperation doubleOperation = new DoubleOperation(0, new Set<>());

    public DoubleOperationConfigurable() {}

    public DoubleOperationConfigurable(@NotNull String operation) {
        fromConfig(operation);
    }

    public DoubleOperationConfigurable(@NotNull DoubleOperation doubleOperation) {
        this.doubleOperation = doubleOperation;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return doubleOperation.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.doubleOperation = Operations.getDoubleOperation((String) configObject);
    }

    public @NotNull DoubleOperation getDoubleOperation() {
        return doubleOperation;
    }

}

