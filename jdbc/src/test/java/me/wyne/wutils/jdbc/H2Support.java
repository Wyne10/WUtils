package me.wyne.wutils.jdbc;

import java.util.concurrent.atomic.AtomicInteger;

/** Shared helpers for tests that run against an in-memory H2 database. */
final class H2Support {

    static final String USER = "sa";
    static final String PASSWORD = "";

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private H2Support() {}

    /** A URL for a fresh in-memory database, isolated from every other test. */
    static String freshUrl() {
        return "jdbc:h2:mem:wutils_" + COUNTER.incrementAndGet() + ";DB_CLOSE_DELAY=-1";
    }
}
