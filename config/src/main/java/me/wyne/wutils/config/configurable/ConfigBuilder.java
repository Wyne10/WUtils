package me.wyne.wutils.config.configurable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConfigBuilder {

    private final Table<Integer, String, String> valueTable = HashBasedTable.create();
    private final Set<Pair<Integer, String>> valueSequence = new LinkedHashSet<>();

    public <TValue> ConfigBuilder append(int depth, String path, @Nullable TValue value)
    {
        if (value == null)
            return this;
        if (value instanceof String stringValue)
            valueTable.put(depth, path, "'" + stringValue + "'");
        else
            valueTable.put(depth, path, value.toString());
        valueSequence.add(new Pair<>(depth, path));
        return this;
    }

    public ConfigBuilder appendObject(int depth, String path, @Nullable Object value)
    {
        if (value == null)
            return this;
        if (value instanceof String stringValue)
            valueTable.put(depth, path, "'" + stringValue + "'");
        else
            valueTable.put(depth, path, value.toString());
        valueSequence.add(new Pair<>(depth, path));
        return this;
    }

    public ConfigBuilder appendCollection(int depth, String path, Collection<?> value)
    {
        if (value.isEmpty())
            return this;
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

    public String build()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        int i = 0;
        for (Pair<Integer, String> entry : valueSequence)
        {
            stringBuilder.append(" ".repeat(entry.getValue0() * 2)).append(entry.getValue1()).append(": ").append(valueTable.get(entry.getValue0(), entry.getValue1()));
            if (i < valueSequence.size() - 1)
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
