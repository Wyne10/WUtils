package me.wyne.wutils.common.event;

public interface RegisterableListener {
    default long getRetentionTicks() {
        if (getClass().isAnnotationPresent(ListenerRetention.class)) {
            return getClass().getDeclaredAnnotation(ListenerRetention.class).ticks();
        }
        return 0;
    }
}
