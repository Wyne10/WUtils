package me.wyne.wutils.i18n.language;

import me.wyne.wutils.log.Log;

public class LogWrapper {

    public final static boolean IS_LOGGER_PRESENT;

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
        if (!IS_LOGGER_PRESENT)
            return;
        Log.global.info(message);
    }

    public static void warn(String message) {
        if (!IS_LOGGER_PRESENT)
            return;
        Log.global.warn(message);
    }

    public static void error(String message) {
        if (!IS_LOGGER_PRESENT)
            return;
        Log.global.error(message);
    }

    public static void exception(Throwable exception) {
        if (!IS_LOGGER_PRESENT)
            return;
        Log.global.exception(exception);
    }

    public static void exception(String message, Throwable exception) {
        if (!IS_LOGGER_PRESENT)
            return;
        Log.global.exception(message, exception);
    }

}
