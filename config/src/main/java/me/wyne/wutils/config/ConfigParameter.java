package me.wyne.wutils.config;

import org.jetbrains.annotations.NotNull;

public interface ConfigParameter {

    void getValue(@NotNull final String path);

}
