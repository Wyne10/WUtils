package me.wyne.wutils.common.particle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/** No-op parser for data types with nothing to configure. Accepts any string and always parses to {@code null}. */
public class VoidDataParser implements StringDataParser<Void> {

    @Override
    public @NotNull Collection<@NotNull String> getSuggestions() {
        return Collections.emptyList();
    }

    /** Always returns {@code null}, regardless of {@code string}. */
    @Override
    public @Nullable Void getData(@NotNull String string) {
        return null;
    }

    @Override
    public @NotNull String toString(@Nullable Object data) {
        return "";
    }

}
