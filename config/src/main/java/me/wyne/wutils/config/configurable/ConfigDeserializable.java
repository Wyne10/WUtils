package me.wyne.wutils.config.configurable;

import org.jetbrains.annotations.Nullable;

/**
 * Specifies configurable which can be deserialized from config in place.
 * <p>
 * Objects that are instead created via a {@code GenericFactory} do not
 * implement this interface and are serialize-only ({@link ConfigSerializable}).
 */
public interface ConfigDeserializable extends ConfigSerializable {

    void fromConfig(@Nullable Object configObject);

}
