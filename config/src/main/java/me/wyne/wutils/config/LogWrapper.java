package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;

public class LogWrapper {

    public final static boolean IS_LOGGER_PRESENT;
    public Log logger;

    public LogWrapper() {
        if (IS_LOGGER_PRESENT)
            logger = Log.global;
    }

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
        if (!IS_LOGGER_PRESENT)
            return;
        logger.info(message);
    }

    public void warn(String message) {
        if (!IS_LOGGER_PRESENT)
            return;
        logger.warn(message);
    }

    public void error(String message) {
        if (!IS_LOGGER_PRESENT)
            return;
        logger.error(message);
    }

    public void exception(Throwable exception) {
        if (!IS_LOGGER_PRESENT)
            return;
        logger.exception(exception);
    }

    public void exception(String message, Throwable exception) {
        if (!IS_LOGGER_PRESENT)
            return;
        logger.exception(message, exception);
    }

}
