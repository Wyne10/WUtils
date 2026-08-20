package me.wyne.wutils.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class HikariConnectionPoolTest {

    private HikariConnectionPool pool() {
        return new HikariConnectionPool(H2Support.freshUrl(), H2Support.USER, H2Support.PASSWORD);
    }

    @Test
    void handsOutUsableConnections() throws Exception {
        try (HikariConnectionPool pool = pool();
             Connection connection = pool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT 1")) {
            assertTrue(result.next());
            assertEquals(1, result.getInt(1));
        }
    }

    @Test
    void getSourceIsNeverNull() throws Exception {
        try (HikariConnectionPool pool = pool()) {
            assertNotNull(pool.getSource());
        }
    }

    @Test
    void isActiveReflectsTheClosedState() throws Exception {
        HikariConnectionPool pool = pool();
        try (Connection ignored = pool.getConnection()) {
            assertTrue(pool.isActive());
        }
        pool.close();
        assertFalse(pool.isActive());
    }

    /** Regression: setAutoCommit(false) was removed, so Hikari's default (on) now applies. */
    @Test
    void autoCommitDefaultsToOn() throws Exception {
        try (HikariConnectionPool pool = pool();
             Connection connection = pool.getConnection()) {
            assertTrue(connection.getAutoCommit());
        }
    }

    @Test
    void configuratorCanTurnAutoCommitBackOff() throws Exception {
        try (HikariConnectionPool pool = pool()) {
            pool.initializeDataSource(source -> source.setAutoCommit(false));
            try (Connection connection = pool.getConnection()) {
                assertFalse(connection.getAutoCommit());
            }
        }
    }

    @Test
    void configuratorReceivesTheLiveDataSource() throws Exception {
        try (HikariConnectionPool pool = pool()) {
            pool.initializeDataSource(source -> source.setPoolName("wutils-test-pool"));
            HikariDataSource source = pool.getSource();
            assertNotNull(source);
            assertEquals("wutils-test-pool", source.getPoolName());
        }
    }

    /** HikariCP seals a data source's configuration once the pool has started. */
    @Test
    void reconfiguringAfterFirstConnectionIsRejected() throws Exception {
        try (HikariConnectionPool pool = pool()) {
            try (Connection ignored = pool.getConnection()) {
                // pool is now running
            }
            assertThrows(IllegalStateException.class,
                    () -> pool.initializeDataSource(source -> source.setAutoCommit(false)));
        }
    }
}
