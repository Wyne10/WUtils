package me.wyne.wutils.config;

public interface Configurable {

    String toConfig(ConfigEntry configEntry);
    void fromConfig(Object configObject);

}
