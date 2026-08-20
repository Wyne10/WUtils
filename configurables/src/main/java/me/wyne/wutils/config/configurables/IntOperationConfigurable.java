package me.wyne.wutils.config.configurables;

import me.wyne.wutils.common.operation.IntOperation;
import me.wyne.wutils.common.operation.Operations;
import me.wyne.wutils.common.operation.Set;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link IntOperation}, read from a string via {@link Operations#getIntOperation}.
 *
 * <p>{@link #fromConfig} casts the incoming value to {@code String} with no {@code instanceof}
 * guard, and a {@code null} config value is a no-op that leaves the previous
 * {@link #getIntOperation()} in place rather than reverting to the constructor default.</p>
 */
public class IntOperationConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private IntOperation intOperation = new IntOperation(0, new Set<>());

    public IntOperationConfigurable() {}

    public IntOperationConfigurable(@NotNull String operation) {
        fromConfig(operation);
    }

    public IntOperationConfigurable(@NotNull IntOperation intOperation) {
        this.intOperation = intOperation;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return intOperation.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.intOperation = Operations.getIntOperation((String) configObject);
    }

    public @NotNull IntOperation getIntOperation() {
        return intOperation;
    }

}

