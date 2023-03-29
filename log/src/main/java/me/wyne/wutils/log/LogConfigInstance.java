package me.wyne.wutils.log;

public class LogConfigInstance implements LogConfig {

    private boolean logInfo = false;
    private boolean logWarn = false;
    private boolean logError = false;

    public LogConfigInstance(final boolean logInfo, final boolean logWarn, final boolean logError)
    {
        this.logInfo = logInfo;
        this.logWarn = logWarn;
        this.logError = logError;
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
    public boolean logError() {
        return logError;
    }

}
