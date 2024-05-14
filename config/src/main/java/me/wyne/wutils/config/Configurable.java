package me.wyne.wutils.config;

public interface Configurable {

    String toConfig();
    String fromConfig(Object configObject);

}
