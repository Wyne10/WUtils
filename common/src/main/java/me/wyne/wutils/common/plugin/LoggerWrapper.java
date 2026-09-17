package me.wyne.wutils.common.plugin;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * An slf4j {@link Logger} decorator that gates every {@code trace}/{@code debug}/{@code info}/
 * {@code warn}/{@code error} family on an explicit {@link LevelWrapper} threshold, independent of
 * however the wrapped logger is itself configured.
 *
 * <p>Every logging call checks the matching {@code isXEnabled} method before logging and is
 * dropped when that level is below the threshold. Once enabled, {@code trace}/{@code debug} calls
 * are forwarded to the wrapped logger's {@code info} methods rather than its
 * {@code trace}/{@code debug} methods; {@code info}/{@code warn}/{@code error} calls are forwarded
 * to the identically-named method on the wrapped logger.</p>
 *
 * <p>Every {@code Marker}-taking {@code isXEnabled(Marker)} override ignores the marker for the
 * enablement check and simply defers to the no-marker overload; the marker is still passed
 * through to the wrapped logger when a message is actually logged.</p>
 */
public class LoggerWrapper implements Logger {

    private final Logger logger;
    private final Level level;

    /**
     * Wraps {@code logger}, gating on {@code level}.
     */
    public LoggerWrapper(@NotNull Logger logger, @NotNull LevelWrapper level) {
        this.logger = logger;
        this.level = level.getLevel();
    }

    @Override
    public @NotNull String getName() {
        return logger.getName();
    }

    /**
     * Returns whether {@code level} is at least as severe as this wrapper's configured
     * threshold, following log4j's ordering (least to most verbose: OFF, FATAL, ERROR, WARN,
     * INFO, DEBUG, TRACE, ALL).
     */
    public boolean isEnabled(@NotNull LevelWrapper level) {
        return level.getLevel().isMoreSpecificThan(this.level);
    }

    @Override
    public boolean isTraceEnabled() {
        return isEnabled(LevelWrapper.TRACE);
    }

    /**
     * Logs {@code msg} at {@code INFO} level on the wrapped logger when {@link #isTraceEnabled()};
     * never forwarded to the wrapped logger's own {@code trace} method.
     */
    @Override
    public void trace(@NotNull String msg) {
        if (!isTraceEnabled())
            return;
        logger.info(msg);
    }

    @Override
    public void trace(@NotNull String format, @NotNull Object arg) {
        if (!isTraceEnabled())
            return;
        logger.info(format, arg);
    }

    @Override
    public void trace(@NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isTraceEnabled())
            return;
        logger.info(format, arg1, arg2);
    }

    @Override
    public void trace(@NotNull String format, @NotNull Object... arguments) {
        if (!isTraceEnabled())
            return;
        logger.info(format, arguments);
    }

    @Override
    public void trace(@NotNull String msg, @NotNull Throwable t) {
        if (!isTraceEnabled())
            return;
        logger.info(msg, t);
    }

    /**
     * Ignores {@code marker}; equivalent to {@link #isTraceEnabled()}.
     */
    @Override
    public boolean isTraceEnabled(@NotNull Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(@NotNull Marker marker, @NotNull String msg) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, msg);
    }

    @Override
    public void trace(@NotNull Marker marker, @NotNull String format, @NotNull Object arg) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, format, arg);
    }

    @Override
    public void trace(@NotNull Marker marker, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, format, arg1, arg2);
    }

    @Override
    public void trace(@NotNull Marker marker, @NotNull String format, @NotNull Object... arguments) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, format, arguments);
    }

    @Override
    public void trace(@NotNull Marker marker, @NotNull String msg, @NotNull Throwable t) {
        if (!isTraceEnabled(marker))
            return;
        logger.info(marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(LevelWrapper.DEBUG);
    }

    /**
     * Behaves like {@link #trace(String)}, gated on {@link #isDebugEnabled()} instead.
     */
    @Override
    public void debug(@NotNull String msg) {
        if (!isDebugEnabled())
            return;
        logger.info(msg);
    }

    @Override
    public void debug(@NotNull String format, @NotNull Object arg) {
        if (!isDebugEnabled())
            return;
        logger.info(format, arg);
    }

    @Override
    public void debug(@NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isDebugEnabled())
            return;
        logger.info(format, arg1, arg2);
    }

    @Override
    public void debug(@NotNull String format, @NotNull Object... arguments) {
        if (!isDebugEnabled())
            return;
        logger.info(format, arguments);
    }

    @Override
    public void debug(@NotNull String msg, @NotNull Throwable t) {
        if (!isDebugEnabled())
            return;
        logger.info(msg, t);
    }

    /**
     * Ignores {@code marker}; equivalent to {@link #isDebugEnabled()}.
     */
    @Override
    public boolean isDebugEnabled(@NotNull Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(@NotNull Marker marker, @NotNull String msg) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, msg);
    }

    @Override
    public void debug(@NotNull Marker marker, @NotNull String format, @NotNull Object arg) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, format, arg);
    }

    @Override
    public void debug(@NotNull Marker marker, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, format, arg1, arg2);
    }

    @Override
    public void debug(@NotNull Marker marker, @NotNull String format, @NotNull Object... arguments) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, format, arguments);
    }

    @Override
    public void debug(@NotNull Marker marker, @NotNull String msg, @NotNull Throwable t) {
        if (!isDebugEnabled(marker))
            return;
        logger.info(marker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(LevelWrapper.INFO);
    }

    @Override
    public void info(@NotNull String msg) {
        if (!isInfoEnabled())
            return;
        logger.info(msg);
    }

    @Override
    public void info(@NotNull String format, @NotNull Object arg) {
        if (!isInfoEnabled())
            return;
        logger.info(format, arg);
    }

    @Override
    public void info(@NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isInfoEnabled())
            return;
        logger.info(format, arg1, arg2);
    }

    @Override
    public void info(@NotNull String format, @NotNull Object... arguments) {
        if (!isInfoEnabled())
            return;
        logger.info(format, arguments);
    }

    @Override
    public void info(@NotNull String msg, @NotNull Throwable t) {
        if (!isInfoEnabled())
            return;
        logger.info(msg, t);
    }

    /**
     * Ignores {@code marker}; equivalent to {@link #isInfoEnabled()}.
     */
    @Override
    public boolean isInfoEnabled(@NotNull Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(@NotNull Marker marker, @NotNull String msg) {
        if (!isInfoEnabled(marker))
            return;
        logger.info(marker, msg);
    }

    @Override
    public void info(@NotNull Marker marker, @NotNull String format, @NotNull Object arg) {
        if (!isInfoEnabled(marker))
            return;
        logger.info(marker, format, arg);
    }

    @Override
    public void info(@NotNull Marker marker, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isInfoEnabled(marker))
            return;
        logger.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(@NotNull Marker marker, @NotNull String format, @NotNull Object... arguments) {
        if (!isInfoEnabled(marker))
            return;
        logger.info(marker, format, arguments);
    }

    @Override
    public void info(@NotNull Marker marker, @NotNull String msg, @NotNull Throwable t) {
        if (!isInfoEnabled(marker))
            return;
        logger.info(marker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled(LevelWrapper.WARN);
    }

    @Override
    public void warn(@NotNull String msg) {
        if (!isWarnEnabled())
            return;
        logger.warn(msg);
    }

    @Override
    public void warn(@NotNull String format, @NotNull Object arg) {
        if (!isWarnEnabled())
            return;
        logger.warn(format, arg);
    }

    @Override
    public void warn(@NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isWarnEnabled())
            return;
        logger.warn(format, arg1, arg2);
    }

    @Override
    public void warn(@NotNull String format, @NotNull Object... arguments) {
        if (!isWarnEnabled())
            return;
        logger.warn(format, arguments);
    }

    @Override
    public void warn(@NotNull String msg, @NotNull Throwable t) {
        if (!isWarnEnabled())
            return;
        logger.warn(msg, t);
    }

    /**
     * Ignores {@code marker}; equivalent to {@link #isWarnEnabled()}.
     */
    @Override
    public boolean isWarnEnabled(@NotNull Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(@NotNull Marker marker, @NotNull String msg) {
        if (!isWarnEnabled(marker))
            return;
        logger.warn(marker, msg);
    }

    @Override
    public void warn(@NotNull Marker marker, @NotNull String format, @NotNull Object arg) {
        if (!isWarnEnabled(marker))
            return;
        logger.warn(marker, format, arg);
    }

    @Override
    public void warn(@NotNull Marker marker, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isWarnEnabled(marker))
            return;
        logger.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(@NotNull Marker marker, @NotNull String format, @NotNull Object... arguments) {
        if (!isWarnEnabled(marker))
            return;
        logger.warn(marker, format, arguments);
    }

    @Override
    public void warn(@NotNull Marker marker, @NotNull String msg, @NotNull Throwable t) {
        if (!isWarnEnabled(marker))
            return;
        logger.warn(marker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled(LevelWrapper.ERROR);
    }

    @Override
    public void error(@NotNull String msg) {
        if (!isErrorEnabled())
            return;
        logger.error(msg);
    }

    @Override
    public void error(@NotNull String format, @NotNull Object arg) {
        if (!isErrorEnabled())
            return;
        logger.error(format, arg);
    }

    @Override
    public void error(@NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isErrorEnabled())
            return;
        logger.error(format, arg1, arg2);
    }

    @Override
    public void error(@NotNull String format, @NotNull Object... arguments) {
        if (!isErrorEnabled())
            return;
        logger.error(format, arguments);
    }

    @Override
    public void error(@NotNull String msg, @NotNull Throwable t) {
        if (!isErrorEnabled())
            return;
        logger.error(msg, t);
    }

    /**
     * Ignores {@code marker}; equivalent to {@link #isErrorEnabled()}.
     */
    @Override
    public boolean isErrorEnabled(@NotNull Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(@NotNull Marker marker, @NotNull String msg) {
        if (!isErrorEnabled(marker))
            return;
        logger.error(marker, msg);
    }

    @Override
    public void error(@NotNull Marker marker, @NotNull String format, @NotNull Object arg) {
        if (!isErrorEnabled(marker))
            return;
        logger.error(marker, format, arg);
    }

    @Override
    public void error(@NotNull Marker marker, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
        if (!isErrorEnabled(marker))
            return;
        logger.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(@NotNull Marker marker, @NotNull String format, @NotNull Object... arguments) {
        if (!isErrorEnabled(marker))
            return;
        logger.error(marker, format, arguments);
    }

    @Override
    public void error(@NotNull Marker marker, @NotNull String msg, @NotNull Throwable t) {
        if (!isErrorEnabled(marker))
            return;
        logger.error(marker, msg, t);
    }

}
