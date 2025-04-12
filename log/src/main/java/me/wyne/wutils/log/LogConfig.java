package me.wyne.wutils.log;

@Deprecated(since = "3.2.0")
public interface LogConfig {

    boolean logInfo();
    boolean logWarn();
    boolean writeInfo();
    boolean writeWarn();

}
