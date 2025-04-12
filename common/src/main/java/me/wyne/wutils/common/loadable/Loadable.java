package me.wyne.wutils.common.loadable;

import org.bukkit.configuration.ConfigurationSection;

public interface Loadable {

    void load(ConfigurationSection config);

    default String getPath() {
        if (getClass().isAnnotationPresent(LoadableMeta.class)) {
            return getClass().getAnnotation(LoadableMeta.class).path();
        }
        return Loader.DEFAULT_PATH;
    }

    default int getPriority() {
        if (getClass().isAnnotationPresent(LoadableMeta.class)) {
            return getClass().getAnnotation(LoadableMeta.class).priority();
        }
        return 0;
    }

}
