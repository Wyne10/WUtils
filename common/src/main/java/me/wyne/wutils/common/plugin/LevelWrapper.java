package me.wyne.wutils.common.plugin;

/**
 * Mirrors the standard {@link org.apache.logging.log4j.Level log4j levels}, from least to most
 * verbose, for use as {@link LoggerWrapper}'s enablement threshold without requiring API
 * consumers to depend on log4j types directly.
 */
public enum LevelWrapper {
    OFF(org.apache.logging.log4j.Level.OFF),
    FATAL(org.apache.logging.log4j.Level.FATAL),
    ERROR(org.apache.logging.log4j.Level.ERROR),
    WARN(org.apache.logging.log4j.Level.WARN),
    INFO(org.apache.logging.log4j.Level.INFO),
    DEBUG(org.apache.logging.log4j.Level.DEBUG),
    TRACE(org.apache.logging.log4j.Level.TRACE),
    ALL(org.apache.logging.log4j.Level.ALL);

    private final org.apache.logging.log4j.Level level;

    LevelWrapper(org.apache.logging.log4j.Level level) {
        this.level = level;
    }

    org.apache.logging.log4j.Level getLevel() {
        return level;
    }
}
