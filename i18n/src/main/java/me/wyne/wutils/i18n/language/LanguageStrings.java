package me.wyne.wutils.i18n.language;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Resolves paths within a {@link Language}'s source, including dotted nested paths where the underlying
 * format supports them (YAML sections, JSON objects). See {@link Language#getStringMap()} for how this
 * differs from the flat string map used for single-string lookups.
 */
public interface LanguageStrings {

    boolean contains(@NotNull String path);

    boolean isList(@NotNull String path);

    @NotNull List<@NotNull String> getStringList(@NotNull String path);

}
