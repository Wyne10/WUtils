package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * An {@link Operation} that already carries its right-hand operand, so callers only
 * need to supply the left-hand value. {@link IntOperation} and {@link DoubleOperation}
 * are the concrete implementations.
 */
public interface ContainedOperation<T extends Number> extends Operation<T> {
    @NotNull T evaluate(@NotNull T leftOperand);
}
