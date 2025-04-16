package me.wyne.wutils.log;

import org.slf4j.Marker;
import org.slf4j.Logger;

import java.util.logging.Level;

/**
 * Slf4j wrapper of deprecated {@link Log} that will be used if server doesn't support log4j
 */
public class JulLogger implements Logger {

    private final Log julLogger;

    public JulLogger(Log logger) {
        this.julLogger = logger;
    }

    private boolean isLoggable(Level level) {
        return julLogger.isLoggable(level);
    }

    @Override
    public String getName() {
        return julLogger.getLogger().getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return isLoggable(Level.FINEST);
    }

    @Override
    public void trace(String msg) {
        julLogger.info(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        julLogger.log(Level.INFO, format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        julLogger.log(Level.INFO, format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        julLogger.log(Level.INFO, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        julLogger.log(Level.INFO, msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String msg) {
        trace(msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        trace(format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        trace(format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... arguments) {
        trace(format, arguments);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        trace(msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return isLoggable(Level.FINE);
    }

    @Override
    public void debug(String msg) {
        julLogger.log(Level.INFO, msg);
    }

    @Override
    public void debug(String format, Object arg) {
        julLogger.log(Level.INFO, format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        julLogger.log(Level.INFO, format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        julLogger.log(Level.INFO, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        julLogger.log(Level.INFO, msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String msg) {
        debug(msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        debug(format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        debug(format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        debug(format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLoggable(Level.INFO);
    }

    @Override
    public void info(String msg) {
        julLogger.log(Level.INFO, msg);
    }

    @Override
    public void info(String format, Object arg) {
        julLogger.log(Level.INFO, format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        julLogger.log(Level.INFO, format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        julLogger.log(Level.INFO, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        julLogger.log(Level.INFO, msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        info(msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        info(format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        info(format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        info(format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String msg) {
        julLogger.log(Level.WARNING, msg);
    }

    @Override
    public void warn(String format, Object arg) {
        julLogger.getLogger().log(Level.WARNING, format, arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        julLogger.log(Level.WARNING, format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments) {
        julLogger.log(Level.WARNING, format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        julLogger.log(Level.WARNING, msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String msg) {
        warn(msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        warn(format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        warn(format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        warn(format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String msg) {
        julLogger.log(Level.SEVERE, msg);
    }

    @Override
    public void error(String format, Object arg) {
        julLogger.log(Level.SEVERE, format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        julLogger.log(Level.SEVERE, format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        julLogger.log(Level.SEVERE, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        julLogger.log(Level.SEVERE, msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String msg) {
        error(msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        error(format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        error(format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        error(format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        error(msg, t);
    }
}
