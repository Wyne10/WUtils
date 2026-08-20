package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * A {@link ListConfigurable} of strings, each converted to a {@code T} via a supplier function
 * rather than read back as {@code T} directly.
 *
 * @param <T> the configurable element type
 */
@Deprecated
public class ListOfConfigurables<T extends ConfigSerializable> extends ListConfigurable<T> {

    private final Function<Object, T> configurableSupplier;

    public ListOfConfigurables(@NotNull Collection<@NotNull T> list, @NotNull Function<@NotNull Object, @NotNull T> configurableSupplier) {
        super(list);
        this.configurableSupplier = configurableSupplier;
    }

    public ListOfConfigurables(@Nullable Object configObject, @NotNull Function<@NotNull Object, @NotNull T> configurableSupplier) {
        super(configObject);
        this.configurableSupplier = configurableSupplier;
    }

    public ListOfConfigurables(@NotNull Function<@NotNull Object, @NotNull T> configurableSupplier) {
        this.configurableSupplier = configurableSupplier;
    }

    @Override
    public @NotNull String toConfig(@NotNull ConfigEntry configEntry) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getList().stream()
                .map(configurable -> configurable.toConfig(configEntry))
                .map(val -> " ".repeat(4) + "- " + val)
                .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        return stringBuilder.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        List<String> config = (List<String>) configObject;
        getList().clear();
        for (int i = 0; i < config.size(); i++) {
            getList().add(configurableSupplier.apply(config.get(i)));
        }
    }

}
