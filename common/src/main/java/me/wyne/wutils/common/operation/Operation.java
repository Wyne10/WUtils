package me.wyne.wutils.common.operation;

import org.jetbrains.annotations.NotNull;

/**
 * A binary arithmetic operation over a {@link Number} type, e.g. {@code +}, {@code -},
 * {@code *}, {@code /}, {@code **}, or a plain assignment ({@link Set}).
 * <p>
 * Implementations ({@link Plus}, {@link Minus}, {@link Multiply}, {@link Divide},
 * {@link Power}, {@link Set}) do not perform arithmetic themselves; they dispatch to
 * the type-specific {@link Operable} for {@code T} via
 * {@link Operations#getOperations(Number)}. Instances are looked up by symbol via
 * {@link Operations#getOperation(String)}.
 */
public interface Operation<T extends Number> {
    @NotNull T evaluate(@NotNull T leftOperand, @NotNull T rightOperand);
}
