package me.wyne.wutils.common.placeholder;

import org.jetbrains.annotations.NotNull;

/**
 * Helpers for building PlaceholderAPI placeholder strings.
 * <p>
 * Only useful alongside the optional PlaceholderAPI {@code compileOnly} dependency.
 */
public final class PAPIUtils {

    /** Joins {@code identifier} and {@code params} into a {@code %identifier_params%}-style raw placeholder body. */
    public static @NotNull String getPlaceholder(@NotNull String identifier, @NotNull String params) {
        return identifier + "_" + params;
    }

}
