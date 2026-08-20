package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A YAML block sequence of {@code E}, rendered as {@code - value} lines.
 *
 * <p>{@link #toConfig(int, ConfigEntry)} throws {@link NoSuchElementException} if the list is empty
 * (it peeks the first element to decide whether to quote as a string, via
 *
 * @param <E> the element type
 */
public class ListConfigurable<E> implements CompositeConfigSerializable, ConfigDeserializable {

    private final List<E> list = new ArrayList<>();

    public ListConfigurable() {}

    public ListConfigurable(@Nullable Object configObject) {
        fromConfig(configObject);
    }

    public ListConfigurable(@NotNull Collection<@NotNull E> list) {
        this.list.addAll(list);
    }

    @Override
    public @NotNull String toConfig(@NotNull ConfigEntry configEntry) {
        return toConfig(1, configEntry);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list.stream().findAny().get() instanceof String)
            stringBuilder.append(list.stream()
                    .map(val -> "'" + val.toString() + "'")
                    .map(val -> " ".repeat(depth * 2) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        else
            stringBuilder.append(list.stream()
                    .map(Object::toString)
                    .map(val -> " ".repeat(depth * 2) + "- " + val)
                    .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        list.clear();
        list.addAll((List<E>)configObject);
    }

    public @NotNull List<@NotNull E> getList() {
        return list;
    }

}
