package me.wyne.wutils.log;

public class BasicLogConfig implements LogConfig {

    private boolean logInfo = false;
    private boolean logWarn = false;
    private boolean writeInfo = false;
    private boolean writeWarn = false;

    public BasicLogConfig(boolean logInfo, boolean logWarn, boolean writeInfo, boolean writeWarn) {
        this.logInfo = logInfo;
        this.logWarn = logWarn;
        this.writeInfo = writeInfo;
        this.writeWarn = writeWarn;
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
    public boolean writeInfo() {
        return writeInfo;
    }

    @Override
    public boolean writeWarn() {
        return writeWarn;
    }
}
