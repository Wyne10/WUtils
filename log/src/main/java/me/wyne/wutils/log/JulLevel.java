package me.wyne.wutils.log;

import java.util.logging.Level;

public enum JulLevel {
    OFF(Level.OFF),
    FATAL(Level.SEVERE),
    ERROR(Level.SEVERE),
    WARN(Level.WARNING),
    INFO(Level.INFO),
    DEBUG(Level.FINE),
    TRACE(Level.FINEST),
    ALL(Level.ALL);

    private final Level level;

    JulLevel(Level level) {
        this.level = level;
    }

    Level getLevel() {
        return level;
    }
}
