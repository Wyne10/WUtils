package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.Configurable;

import java.util.*;

public class  ListConfigurable<T> implements Configurable {

    private final List<T> list = new ArrayList<>();

    public ListConfigurable(Collection<T> list) {
        this.list.addAll(list);
    }

    public ListConfigurable(Object configObject) {
        fromConfig(configObject);
    }

    public ListConfigurable() {}

    @Override
    public String toConfig(ConfigEntry configEntry) {
        StringBuilder builder = new StringBuilder();
        if (list.stream().findAny().get() instanceof String)
            builder.append(list.stream()
                    .map(val -> "'" + val.toString() + "'")
                    .map(val -> " ".repeat(4) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        else
            builder.append(list.stream()
                    .map(Object::toString)
                    .map(val -> " ".repeat(4) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromConfig(Object configObject) {
        list.clear();
        list.addAll((List<T>)configObject);
    }

    public List<T> getList() {
        return list;
    }

}
