package me.wyne.wutils.common.plugin;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class LoggerWrapper implements Logger {

    private final Logger logger;
    private final Level level;

    public LoggerWrapper(Logger logger, LevelWrapper level) {
        this.logger = logger;
        this.level = level.getLevel();
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    public boolean isEnabled(LevelWrapper level) {
        return level.getLevel().isMoreSpecificThan(this.level);
    }

    @Override
    public boolean isTraceEnabled() {
        return isEnabled(LevelWrapper.TRACE);
    }

    @Override
    public void trace(String msg) {
        if (!isTraceEnabled())
            return;
        logger.info(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        if (!isTraceEnabled())
            return;
        logger.info(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (!isTraceEnabled())
            return;
        logger.info(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (!isTraceEnabled())
            return;
        logger.info(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (!isTraceEnabled())
            return;
        logger.info(msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... arguments) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, format, arguments);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(LevelWrapper.DEBUG);
    }

    @Override
    public void debug(String msg) {
        if (!isDebugEnabled())
            return;
        logger.info(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        if (!isDebugEnabled())
            return;
        logger.info(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (!isDebugEnabled())
            return;
        logger.info(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (!isDebugEnabled())
            return;
        logger.info(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (!isDebugEnabled())
            return;
        logger.info(msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(LevelWrapper.INFO);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        logger.info(marker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(marker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        logger.info(marker, format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        logger.info(marker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled(LevelWrapper.WARN);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(format, arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.warn(format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String msg) {
        logger.warn(marker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(marker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        logger.warn(marker, format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        logger.warn(marker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled(LevelWrapper.ERROR);
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String msg) {
        logger.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        logger.error(marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        logger.error(marker, msg, t);
    }

}
