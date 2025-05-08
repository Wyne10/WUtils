package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ListConfigurable<E> implements CompositeConfigurable {

    private final List<E> list = new ArrayList<>();

    public ListConfigurable() {}

    public ListConfigurable(Object configObject) {
        fromConfig(configObject);
    }

    public ListConfigurable(Collection<E> list) {
        this.list.addAll(list);
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
        return toConfig(1, configEntry);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list.stream().findAny().get() instanceof String)
            stringBuilder.append(list.stream()
                    .map(val -> "'" + val.toString() + "'")
                    .map(val -> " ".repeat(depth * 4) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        else
            stringBuilder.append(list.stream()
                    .map(Object::toString)
                    .map(val -> " ".repeat(depth * 4) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromConfig(@Nullable Object configObject) {
        list.clear();
        if (configObject == null)
            return;
        list.addAll((List<E>)configObject);
    }

    public List<E> getList() {
        return list;
    }

}
