package me.wyne.wutils.i18n.language.validation;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Returns {@code null} for a missing path instead of a fallback, deliberately violating
 * {@link StringValidator}'s non-null contract.
 *
 * <p>This is a "see what happens" implementation, not a production choice. Nothing downstream is
 * written to cope with it: a {@code StringInterpreter}/{@code ComponentInterpreter} built with this
 * validator lets the {@code null} escape through methods annotated and documented as returning
 * non-null, so the failure surfaces later and somewhere else — a {@link NullPointerException} deeper in
 * the interpreter, a null slipping into a message, or an immediate crash at a Kotlin call site that
 * trusted the non-null annotation.</p>
 *
 * <p>Use it when you specifically want a missing path to blow up in an unstructured way while poking at
 * behavior. For everything else use {@link EmptyValidator} (the default), or {@link ReplaceValidator} /
 * {@link ExceptionValidator} when you want a fixed substitute or a clean, immediate error.</p>
 */
public class NullValidator implements StringValidator {

    /**
     * {@inheritDoc}
     *
     * <p>Returns {@code null} when {@code path} is missing, in violation of the interface's non-null
     * contract. The annotation is inherited and is a lie here — that is the point of this class.</p>
     */
    @Override
    public @NotNull String validateString(@NotNull Map<@NotNull String, @NotNull String> strings, @NotNull String path) {
        return strings.get(path);
    }
}
