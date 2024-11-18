package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;

public class LogWrapper {

    public final static boolean IS_LOGGER_PRESENT;
    public static Log LOGGER;

    static {
        boolean isLoggerPresent;
        try {
            Class.forName("me.wyne.wutils.log.Log");
            isLoggerPresent = true;
            LOGGER = Log.global;
        } catch (ClassNotFoundException e) {
            isLoggerPresent = false;
        }
        IS_LOGGER_PRESENT = isLoggerPresent;
    }

    public static void info(String message) {
        if (!IS_LOGGER_PRESENT)
            return;
        LOGGER.info(message);
    }

    public static void warn(String message) {
        if (!IS_LOGGER_PRESENT)
            return;
        LOGGER.warn(message);
    }

    public static void error(String message) {
        if (!IS_LOGGER_PRESENT)
            return;
        LOGGER.error(message);
    }

    public static void exception(Throwable exception) {
        if (!IS_LOGGER_PRESENT)
            return;
        LOGGER.exception(exception);
    }

    public static void exception(String message, Throwable exception) {
        if (!IS_LOGGER_PRESENT)
            return;
        LOGGER.exception(message, exception);
    }

}
