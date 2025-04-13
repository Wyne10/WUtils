package me.wyne.wutils.log;

public class BasicLogConfig implements LogConfig {

    private boolean logInfo = false;
    private boolean logWarn = false;
    private boolean logDebug = false;
    private boolean writeInfo = false;
    private boolean writeWarn = false;
    private boolean writeDebug = false;

    public BasicLogConfig(boolean logInfo, boolean logWarn, boolean logDebug, boolean writeInfo, boolean writeWarn, boolean writeDebug) {
        this.logInfo = logInfo;
        this.logWarn = logWarn;
        this.logDebug = logDebug;
        this.writeInfo = writeInfo;
        this.writeWarn = writeWarn;
        this.writeDebug = writeDebug;
    }

    @Override
    public boolean logInfo() {
        return logInfo;
    }

    @Override
    public boolean logWarn() {
        return logWarn;
    }

    @Override
    public boolean logDebug() {
        return logDebug;
    }

    @Override
    public boolean writeInfo() {
        return writeInfo;
    }

    @Override
    public boolean writeWarn() {
        return writeWarn;
    }

    @Override
    public boolean writeDebug() {
        return writeDebug;
    }
}
