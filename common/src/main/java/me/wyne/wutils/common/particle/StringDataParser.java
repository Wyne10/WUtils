package me.wyne.wutils.common.particle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Converts between a single command-argument-style string token and a typed particle data value
 * of type {@code T}, for a specific data class. Implementations are looked up by data class via
 * {@link DataParserProvider#getDataParser}; each accepts its own string syntax and fails
 * differently on malformed input (some return {@code null}, some throw) — see the implementing
 * class for details.
 */
public interface StringDataParser<T> {

     /** Example/completion values for the string form this parser accepts. */
     @NotNull Collection<@NotNull String> getSuggestions();

     /** Parses {@code string} into a {@code T}. Whether malformed input yields {@code null} or throws depends on the implementation. */
     @Nullable T getData(@NotNull String string);

     /** Renders a previously parsed value back to its string form. */
     @NotNull String toString(@Nullable Object data);

}
