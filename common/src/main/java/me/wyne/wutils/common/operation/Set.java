package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * Assignment, not arithmetic: ignores {@code leftOperand} and returns
 * {@code rightOperand} unchanged. The default {@link Operation} for an unrecognized
 * or absent operator symbol; see {@link Operations#getOperation(String)}.
 */
public class Set<T extends Number> implements Operation<T> {
    @Override
    public @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand) {
        return rightOperand;
    }
}
