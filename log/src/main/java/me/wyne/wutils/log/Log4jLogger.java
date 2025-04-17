package me.wyne.wutils.log;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Slf4j wrapper of log4j logger that makes debug and trace logs go to info level
 */
public class Log4jLogger implements Logger {

    private final Logger parent;

    public Log4jLogger(Logger logger) {
        this.parent = logger;
    }

    @Override
    public String getName() {
        return parent.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return parent.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        if (!isTraceEnabled())
            return;
        parent.info(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        if (!isTraceEnabled())
            return;
        parent.info(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (!isTraceEnabled())
            return;
        parent.info(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (!isTraceEnabled())
            return;
        parent.info(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (!isTraceEnabled())
            return;
        parent.info(msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return parent.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (!isTraceEnabled(marker))
            return;
        parent.info(marker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (!isTraceEnabled(marker))
            return;
        parent.info(marker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (!isTraceEnabled(marker))
            return;
        parent.info(marker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... arguments) {
        if (!isTraceEnabled(marker))
            return;
        parent.info(marker, format, arguments);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (!isTraceEnabled(marker))
            return;
        parent.info(marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return parent.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        if (!isDebugEnabled())
            return;
        parent.info(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        if (!isDebugEnabled())
            return;
        parent.info(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (!isDebugEnabled())
            return;
        parent.info(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (!isDebugEnabled())
            return;
        parent.info(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (!isDebugEnabled())
            return;
        parent.info(msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return parent.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (!isDebugEnabled(marker))
            return;
        parent.info(marker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (!isDebugEnabled(marker))
            return;
        parent.info(marker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (!isDebugEnabled(marker))
            return;
        parent.info(marker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (!isDebugEnabled(marker))
            return;
        parent.info(marker, format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (!isDebugEnabled(marker))
            return;
        parent.info(marker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return parent.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        parent.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        parent.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        parent.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        parent.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        parent.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return parent.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        parent.info(marker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        parent.info(marker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        parent.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        parent.info(marker, format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        parent.info(marker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return parent.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        parent.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        parent.warn(format, arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        parent.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments) {
        parent.warn(format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        parent.warn(msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return parent.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        parent.warn(marker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        parent.warn(marker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        parent.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        parent.warn(marker, format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        parent.warn(marker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return parent.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        parent.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        parent.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        parent.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        parent.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        parent.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return parent.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        parent.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        parent.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        parent.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        parent.error(marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        parent.error(marker, msg, t);
    }

}

