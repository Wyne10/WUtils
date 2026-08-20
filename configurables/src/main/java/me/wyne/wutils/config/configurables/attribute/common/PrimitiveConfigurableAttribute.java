package me.wyne.wutils.config.configurables.attribute.common;

import com.google.common.base.Preconditions;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link ConfigurableAttribute} counterpart to {@link PrimitiveAttribute} — same raw
 * {@link ConfigurationSection#get} value, but rendered back into generated config as {@code key:
 * value} since it extends {@link ConfigurableAttribute} rather than plain {@code AttributeBase}.
 *
 * @param <V> the value type
 */
public class PrimitiveConfigurableAttribute<V> extends ConfigurableAttribute<V> {

    public PrimitiveConfigurableAttribute(@NotNull String key, @NotNull V value) {
        super(key, value);
    }

    public static final class Factory implements AttributeFactory<PrimitiveConfigurableAttribute<?>> {
        @Override
        public @NotNull PrimitiveConfigurableAttribute<?> create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new PrimitiveConfigurableAttribute<>(key, Preconditions.checkNotNull(config.get(key),
                    "No value provided for " + ConfigUtils.getPath(config, key)));
        }
    }

}
