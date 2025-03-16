package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.Configurable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ListOfConfigurables<T extends Configurable> extends ListConfigurable<T> {

    private final Function<Object, T> configurableSupplier;

    public ListOfConfigurables(Collection<T> list, Function<Object, T> configurableSupplier) {
        super(list);
        this.configurableSupplier = configurableSupplier;
    }

    public ListOfConfigurables(Object configObject, Function<Object, T> configurableSupplier) {
        super(configObject);
        this.configurableSupplier = configurableSupplier;
    }

    public ListOfConfigurables(Function<Object, T> configurableSupplier) {
        this.configurableSupplier = configurableSupplier;
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
        StringBuilder builder = new StringBuilder();
        builder.append(getList().stream()
                .map(configurable -> configurable.toConfig(configEntry))
                .map(val -> " ".repeat(4) + "- " + val)
                .reduce("", ((s1, s2) -> s1 + "\n" + s2)));
        return builder.toString();
    }

    @Override
    public void fromConfig(Object configObject) {
        List<String> config = (List<String>) configObject;
        getList().clear();
        for (int i = 0; i < config.size(); i++) {
            getList().add(configurableSupplier.apply(config.get(i)));
        }
    }

}
