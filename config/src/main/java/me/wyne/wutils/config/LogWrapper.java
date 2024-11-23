package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;

public class LogWrapper {

    public final static boolean IS_LOGGER_PRESENT;
    public Log logger;

    static {
        boolean isLoggerPresent;
        try {
            Class.forName("me.wyne.wutils.log.Log");
            isLoggerPresent = true;
        } catch (ClassNotFoundException e) {
            isLoggerPresent = false;
        }
        IS_LOGGER_PRESENT = isLoggerPresent;
    }

    public void info(String message) {
        if (!isLoggerPresent())
            return;
        logger.info(message);
    }

    public void warn(String message) {
        if (!isLoggerPresent())
            return;
        logger.warn(message);
    }

    public void error(String message) {
        if (!isLoggerPresent())
            return;
        logger.error(message);
    }

    public void exception(Throwable exception) {
        if (!isLoggerPresent())
            return;
        logger.exception(exception);
    }

    public void exception(String message, Throwable exception) {
        if (!isLoggerPresent())
            return;
        logger.exception(message, exception);
    }
    
    private boolean isLoggerPresent() {
        if (IS_LOGGER_PRESENT && logger == null)
            logger = Log.global;
        return IS_LOGGER_PRESENT;
    }

}
