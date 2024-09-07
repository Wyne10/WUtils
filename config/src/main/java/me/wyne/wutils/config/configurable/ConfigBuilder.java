package me.wyne.wutils.config.configurable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigBuilder {

    private final Table<Integer, String, String> valueTable = HashBasedTable.create();
    private final Map<Integer, String> valueSequence = new LinkedHashMap<>();

    public <TValue> ConfigBuilder append(int depth, String path, @Nullable TValue value)
    {
        if (value == null)
            return this;
        valueTable.put(depth, path, value.toString());
        valueSequence.put(depth, path);
        return this;
    }

    public ConfigBuilder appendObject(int depth, String path, @Nullable Object value)
    {
        if (value == null)
            return this;
        valueTable.put(depth, path, value.toString());
        valueSequence.put(depth, path);
        return this;
    }

    public ConfigBuilder appendList(int depth, String path, List<?> value)
    {
        if (value.isEmpty())
            return this;
        valueTable.put(depth, path, value.stream()
                .map(val -> value.toString())
                .map(val -> " ".repeat(depth * 2) + "- " + val)
                .reduce("\n", ((s1, s2) -> s1 + "\n" + s2)));
        valueSequence.put(depth, path);
        return this;
    }

    public String build()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Integer, String> entry : valueSequence.entrySet())
        {
            stringBuilder.append(" ".repeat(entry.getKey() * 2)).append(valueTable.get(entry.getKey(), entry.getValue()));
        }
        return stringBuilder.toString();
    }
}
