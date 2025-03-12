package me.wyne.wutils.jdbc;

import me.wyne.wutils.log.Log;

public final class LogWrapper {

    public final static boolean IS_LOGGER_PRESENT;
    private static Log logger;

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

    public static void info(String message) {
        if (!isLoggerPresent())
            return;
        logger.info(message);
    }

    public static void warn(String message) {
        if (!isLoggerPresent())
            return;
        logger.warn(message);
    }

    public static void error(String message) {
        if (!isLoggerPresent())
            return;
        logger.error(message);
    }

    public static void exception(Throwable exception) {
        if (!isLoggerPresent())
            return;
        logger.exception(exception);
    }

    public static void exception(String message, Throwable exception) {
        if (!isLoggerPresent())
            return;
        logger.exception(message, exception);
    }

    private static boolean isLoggerPresent() {
        if (IS_LOGGER_PRESENT && logger == null)
            logger = Log.global;
        return IS_LOGGER_PRESENT;
    }

}
