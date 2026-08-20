package me.wyne.wutils.jdbc;

import com.j256.ormlite.support.ConnectionSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class HikariOrmLiteConnectionPoolTest {

    private HikariOrmLiteConnectionPool pool() throws Exception {
        return new HikariOrmLiteConnectionPool(H2Support.freshUrl(), H2Support.USER, H2Support.PASSWORD);
    }

    @Test
    void handsOutUsableConnections() throws Exception {
        try (HikariOrmLiteConnectionPool pool = pool();
             Connection connection = pool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT 1")) {
            assertTrue(result.next());
            assertEquals(1, result.getInt(1));
        }
    }

    @Test
    void getSourceExposesTheOrmLiteConnectionSource() throws Exception {
        try (HikariOrmLiteConnectionPool pool = pool()) {
            ConnectionSource source = pool.getSource();
            assertNotNull(source);
        }
    }

    @Test
    void isActiveReflectsTheClosedState() throws Exception {
        HikariOrmLiteConnectionPool pool = pool();
        assertTrue(pool.isActive());
        pool.close();
        assertFalse(pool.isActive());
    }

    /** Both Hikari-backed pools now share the same autoCommit default. */
    @Test
    void autoCommitDefaultsToOn() throws Exception {
        try (HikariOrmLiteConnectionPool pool = pool();
             Connection connection = pool.getConnection()) {
            assertTrue(connection.getAutoCommit());
        }
    }

    /**
     * The configurator must run before the ConnectionSource is built over the data source.
     * Building it starts the Hikari pool, so this only works through the constructor.
     */
    @Test
    void constructorConfiguratorAppliesToLaterConnections() throws Exception {
        try (HikariOrmLiteConnectionPool pool = new HikariOrmLiteConnectionPool(
                H2Support.freshUrl(), H2Support.USER, H2Support.PASSWORD,
                source -> source.setAutoCommit(false))) {
            try (Connection connection = pool.getConnection()) {
                assertFalse(connection.getAutoCommit());
            }
            assertNotNull(pool.getSource());
        }
    }

    /** Construction starts the pool, which seals Hikari's configuration. */
    @Test
    void reconfiguringAfterConstructionIsRejected() throws Exception {
        try (HikariOrmLiteConnectionPool pool = pool()) {
            assertThrows(IllegalStateException.class,
                    () -> pool.initializeDataSource(source -> source.setAutoCommit(false)));
        }
    }
}
