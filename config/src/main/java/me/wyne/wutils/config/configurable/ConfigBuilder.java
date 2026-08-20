package me.wyne.wutils.config.configurable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.wyne.wutils.config.ConfigEntry;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Builds a piece of generated YAML text out of an ordered set of {@code path: value} lines.
 *
 * <p>Values are keyed internally by {@code (depth, path)}: appending the same path at the same depth
 * twice overwrites the earlier value, but the position it is rendered at is fixed by the first
 * append — insertion order (tracked separately) is not affected by the overwrite.</p>
 */
@SuppressWarnings("UnusedReturnValue")
public class ConfigBuilder {

    /**
     * <pre>
     * Default depth is set to 2 due to how config is generated.
     * Config example:
     *
     * section: (depth 0)
     *   item: (depth 1)
     *     parameter: 5 (depth 2)
     * </pre>
     */
    public final static int DEFAULT_DEPTH = 2;

    private final Table<Integer, String, String> valueTable = HashBasedTable.create();
    private final Set<Pair<Integer, String>> valueSequence = new LinkedHashSet<>();

    /**
     * Appends {@code path: value} at {@link #DEFAULT_DEPTH}. A {@code null} value is a no-op — the key
     * is simply omitted from the output, not written with an empty or null value.
     */
    public <T> @NotNull ConfigBuilder append(@NotNull String path, @Nullable T value) {
        return append(DEFAULT_DEPTH, path, value);
    }

    /**
     * Appends {@code path: value} at the given depth. A {@code null} value is a no-op. A
     * {@link Collection} value is delegated to {@link #appendCollection}; a {@link String} value is
     * quoted with single quotes; anything else is written via {@link Object#toString()}.
     */
    public <T> @NotNull ConfigBuilder append(int depth, @NotNull String path, @Nullable T value) {
        if (value == null)
            return this;
        if (value instanceof Collection<?>)
            return appendCollection(depth, path, (Collection<?>) value);
        if (value instanceof String stringValue)
            valueTable.put(depth, path, "'" + stringValue + "'");
        else
            valueTable.put(depth, path, value.toString());
        valueSequence.add(new Pair<>(depth, path));
        return this;
    }

    /**
     * Appends {@code path: value} at {@link #DEFAULT_DEPTH}, writing {@code value} raw with no
     * quoting, even if it looks like a YAML string. A {@code null} value is a no-op.
     */
    public @NotNull ConfigBuilder appendString(@NotNull String path, @Nullable String value) {
        return appendString(DEFAULT_DEPTH, path, value);
    }

    /**
     * Appends {@code path: value} at the given depth, writing {@code value} raw with no quoting. A
     * {@code null} value is a no-op.
     */
    public @NotNull ConfigBuilder appendString(int depth, @NotNull String path, @Nullable String value) {
        if (value == null)
            return this;
        valueTable.put(depth, path, value);
        valueSequence.add(new Pair<>(depth, path));
        return this;
    }

    /**
     * Appends {@code path: value} at {@link #DEFAULT_DEPTH}, unless {@code value} is {@code null} or
     * {@link Object#equals equal} to {@code otherValue}, in either of which cases this is a no-op.
     */
    public <T> @NotNull ConfigBuilder appendIfNotEqual(@NotNull String path, @Nullable T value, @NotNull T otherValue) {
        return appendIfNotEqual(DEFAULT_DEPTH, path, value, otherValue);
    }

    /**
     * Appends {@code path: value} at the given depth, unless {@code value} is {@code null} or
     * {@link Object#equals equal} to {@code otherValue}, in either of which cases this is a no-op.
     * Quotes a {@link String} value the same way {@link #append} does.
     */
    public <T> @NotNull ConfigBuilder appendIfNotEqual(int depth, @NotNull String path, @Nullable T value, @NotNull T otherValue) {
        if (value == null)
            return this;
        if (value.equals(otherValue))
            return this;
        if (value instanceof String stringValue)
            valueTable.put(depth, path, "'" + stringValue + "'");
        else
            valueTable.put(depth, path, value.toString());
        valueSequence.add(new Pair<>(depth, path));
        return this;
    }

    /**
     * Appends a collection as a YAML block sequence at {@link #DEFAULT_DEPTH}.
     */
    public @NotNull ConfigBuilder appendCollection(@NotNull String path, @NotNull Collection<?> value) {
        return appendCollection(DEFAULT_DEPTH, path, value);
    }

    /**
     * Appends a collection as a YAML block sequence at the given depth.
     *
     * <p>An empty collection is skipped entirely (no key is written). A single-element collection is
     * instead written inline as {@code [element]} via {@link #appendString}, meaning — unlike the
     * multi-element path below — a lone {@link String} element is <em>not</em> quoted. For two or more
     * elements, each is rendered on its own {@code - } line, quoted with single quotes if (based on an
     * arbitrary element of the collection) the elements are {@link String}s.</p>
     */
    public @NotNull ConfigBuilder appendCollection(int depth, @NotNull String path, @NotNull Collection<?> value) {
        if (value.isEmpty())
            return this;
        if (value.size() == 1)
            return appendString(depth, path, "[" + value.iterator().next() + "]");
        if (value.stream().findAny().get() instanceof String)
            valueTable.put(depth, path, value.stream()
                    .map(val -> "'" + val.toString() + "'")
                    .map(val -> " ".repeat((depth + 1) * 2) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        else
            valueTable.put(depth, path, value.stream()
                    .map(Object::toString)
                    .map(val -> " ".repeat((depth + 1) * 2) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        valueSequence.add(new Pair<>(depth, path));
        return this;
    }

    /**
     * Appends a {@link CompositeConfigSerializable}'s own rendered config, nested one depth below
     * {@code depth}, unless it renders to an empty string, in which case this is a no-op.
     */
    public @NotNull ConfigBuilder appendComposite(int depth, @NotNull String path, @NotNull CompositeConfigSerializable value, @NotNull ConfigEntry configEntry) {
        String string = value.toConfig(depth + 1, configEntry);
        if (!string.isEmpty()) {
            return appendString(depth, path, string);
        }
        return this;
    }

    /**
     * Builds with a leading and a trailing newline. Equivalent to {@code build(false, false)}.
     */
    public @NotNull String build() {
        return build(false, false);
    }

    /**
     * Builds without a leading newline, but with a trailing one. Equivalent to
     * {@code build(true, false)}.
     */
    public @NotNull String buildNoSpace() {
        return build(true, false);
    }

    /**
     * Builds with a leading newline, but without a trailing one. Equivalent to
     * {@code build(false, true)}.
     */
    public @NotNull String buildNoTrail() {
        return build(false, true);
    }

    /**
     * Renders every appended {@code path: value} line in insertion order, indented by {@code depth * 2}
     * spaces. Returns an empty string if nothing was appended.
     *
     * @param skipSpace omit the leading newline
     * @param skipTrailing omit the trailing newline
     */
    public @NotNull String build(boolean skipSpace, boolean skipTrailing) {
        if (valueSequence.isEmpty())
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        if (!skipSpace)
            stringBuilder.append("\n");
        int i = 0;
        for (Pair<Integer, String> entry : valueSequence) {
            stringBuilder.append(" ".repeat(entry.getValue0() * 2)).append(entry.getValue1()).append(": ").append(valueTable.get(entry.getValue0(), entry.getValue1()));
            if (i < valueSequence.size() - 1) {
                stringBuilder.append("\n");
            } else {
                if (!skipTrailing)
                    stringBuilder.append("\n");
            }
            i++;
        }
        return stringBuilder.toString();
    }

    @Override
    public @NotNull String toString() {
        return build();
    }

}
