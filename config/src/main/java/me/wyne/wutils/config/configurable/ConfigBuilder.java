package me.wyne.wutils.config.configurable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.wyne.wutils.config.ConfigEntry;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    public <T> ConfigBuilder append(String path, @Nullable T value) {
        return append(DEFAULT_DEPTH, path, value);
    }

    public <T> ConfigBuilder append(int depth, String path, @Nullable T value) {
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

    public ConfigBuilder appendString(String path, @Nullable String value) {
        return appendString(DEFAULT_DEPTH, path, value);
    }

    public ConfigBuilder appendString(int depth, String path, @Nullable String value) {
        if (value == null)
            return this;
        valueTable.put(depth, path, value);
        valueSequence.add(new Pair<>(depth, path));
        return this;
    }

    public <T> ConfigBuilder appendIfNotEqual(String path, @Nullable T value, T otherValue) {
        return appendIfNotEqual(DEFAULT_DEPTH, path, value, otherValue);
    }

    public <T> ConfigBuilder appendIfNotEqual(int depth, String path, @Nullable T value, T otherValue) {
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

    public ConfigBuilder appendCollection(String path, Collection<?> value) {
        return appendCollection(DEFAULT_DEPTH, path, value);
    }

    public ConfigBuilder appendCollection(int depth, String path, Collection<?> value) {
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

    public ConfigBuilder appendComposite(int depth, String path, CompositeConfigurable value, ConfigEntry configEntry) {
        String string = value.toConfig(depth + 1, configEntry);
        if (!string.isEmpty()) {
            return appendString(depth, path, string);
        }
        return this;
    }

    public String build() {
        return build(false, false);
    }

    public String buildNoSpace() {
        return build(true, false);
    }

    public String buildNoTrail() {
        return build(false, true);
    }

    public String build(boolean skipSpace, boolean skipTrailing) {
        if (valueSequence.isEmpty())
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        if (!skipSpace)
            stringBuilder.append("\n");
        int i = 0;
        for (Pair<Integer, String> entry : valueSequence) {
            stringBuilder.append(" ".repeat(entry.getValue0() * 2)).append(entry.getValue1()).append(": ").append(valueTable.get(entry.getValue0(), entry.getValue1()));
            if (i >= valueSequence.size() - 1 && !skipTrailing)
                stringBuilder.append("\n");
            i++;
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return build();
    }

}
