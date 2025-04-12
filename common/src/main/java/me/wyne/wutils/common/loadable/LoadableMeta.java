package me.wyne.wutils.common.loadable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadableMeta {

    String path() default Loader.DEFAULT_PATH;
    int priority() default 0;

}
