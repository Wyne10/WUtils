package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import com.google.common.base.Preconditions;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * A read-only attribute holding whatever raw value {@link ConfigurationSection#get} returns for its
 * key — a {@code String}, number, boolean, list, or {@code null} if the key holds an explicit YAML
 * {@code null}. Not a {@link me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute}, so
 * it never appears in generated config.
 *
 * @param <V> the value type
 */
public class PrimitiveAttribute<V> extends AttributeBase<V> {

    public PrimitiveAttribute(@NotNull String key, @NotNull V value) {
        super(key, value);
    }

    public static final class Factory implements AttributeFactory<PrimitiveAttribute<?>> {
        @Override
        public @NotNull PrimitiveAttribute<?> create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new PrimitiveAttribute<>(key, Preconditions.checkNotNull(config.get(key),
                    "No value provided for " + ConfigUtils.getPath(config, key)));
        }
    }

}
