package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.Configurable;

import java.util.*;

public class ListConfigurable<E> implements Configurable {

    private final List<E> list = new ArrayList<>();

    public ListConfigurable(Collection<E> list) {
        this.list.addAll(list);
    }

    public ListConfigurable(Object configObject) {
        fromConfig(configObject);
    }

    public ListConfigurable() {}

    @Override
    public String toConfig(ConfigEntry configEntry) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list.stream().findAny().get() instanceof String)
            stringBuilder.append(list.stream()
                    .map(val -> "'" + val.toString() + "'")
                    .map(val -> " ".repeat(4) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        else
            stringBuilder.append(list.stream()
                    .map(Object::toString)
                    .map(val -> " ".repeat(4) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromConfig(Object configObject) {
        list.clear();
        list.addAll((List<E>)configObject);
    }

    public List<E> getList() {
        return list;
    }

}
