package me.wyne.wutils.i18n.language.validation;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Resolves a raw lookup {@code path} against a {@link me.wyne.wutils.i18n.language.Language}'s flat string map (see
 * {@link me.wyne.wutils.i18n.language.Language#getStringMap()}), deciding what happens when the path is
 * missing: fall back to a placeholder or throw.
 *
 * <p>Implementations must return non-null. Passed to a {@code StringInterpreter}/
 * {@code ComponentInterpreter}, whose own methods advertise a non-null return and rely on this one
 * holding up.</p>
 *
 * <p><b>{@link EmptyValidator} is the right choice almost always</b> — it is the default, and returning
 * the missing path itself makes an absent string obvious in-game without breaking anything.
 * {@link ReplaceValidator} substitutes a fixed string instead, and {@link ExceptionValidator} turns a
 * missing path into a hard failure for setups that would rather not ship with gaps.
 * {@link NullValidator} deliberately violates this contract; see its documentation before reaching
 * for it.</p>
 */
@FunctionalInterface
public interface StringValidator {

    /**
     * Returns the value at {@code path}, or the implementation's chosen fallback if {@code path} is
     * missing from {@code strings}. Must not return {@code null}.
     */
    @NotNull String validateString(@NotNull Map<@NotNull String, @NotNull String> strings, @NotNull String path);

}
