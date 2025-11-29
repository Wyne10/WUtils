package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;

import java.util.Map;

public interface AttributeConfigurableAccessor<T extends AttributeConfigurable> {
    T ignore(String... ignore);

    T with(String key, AttributeFactory<?> factory);

    T with(Map<String, AttributeFactory<?>> keyMap);

    T with(Attribute<?> attribute);

    T with(AttributeContainer container);

    T copy(AttributeContainer container);

    T copy();
}
