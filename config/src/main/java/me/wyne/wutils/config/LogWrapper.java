package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;
import org.slf4j.Logger;

public final class LogWrapper {

    public final static boolean IS_LOGGER_PRESENT;
    public Log logger;
    public Logger slf4jLogger;

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
        if (isLoggerPresent())
            logger.info(message);
        if (slf4jLogger != null)
            slf4jLogger.info(message);
    }

    public void warn(String message) {
        if (isLoggerPresent())
            logger.warn(message);
        if (slf4jLogger != null)
            slf4jLogger.warn(message);
    }

    public void error(String message) {
        if (isLoggerPresent())
            logger.error(message);
        if (slf4jLogger != null)
            slf4jLogger.error(message);
    }

    public void exception(Throwable exception) {
        if (isLoggerPresent())
            logger.exception(exception);
        if (slf4jLogger != null)
            slf4jLogger.error("", exception);
    }

    public void exception(String message, Throwable exception) {
        if (isLoggerPresent())
            logger.exception(message, exception);
        if (slf4jLogger != null)
            slf4jLogger.error(message, exception);
    }
    
    private boolean isLoggerPresent() {
        return IS_LOGGER_PRESENT && logger != null;
    }

}
