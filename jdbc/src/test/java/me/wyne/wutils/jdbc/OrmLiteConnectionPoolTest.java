package me.wyne.wutils.jdbc;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class OrmLiteConnectionPoolTest {

    @DatabaseTable(tableName = "widget")
    static class Widget {
        @DatabaseField(id = true)
        String name;

        Widget() {}

        Widget(String name) {
            this.name = name;
        }
    }

    private OrmLiteConnectionPool pool() throws Exception {
        return new OrmLiteConnectionPool(H2Support.freshUrl(), H2Support.USER, H2Support.PASSWORD);
    }

    /** Regression: this used to throw NullPointerException to signal "unsupported". */
    @Test
    void getConnectionIsUnsupported() throws Exception {
        try (OrmLiteConnectionPool pool = pool()) {
            assertThrows(UnsupportedOperationException.class, pool::getConnection);
        }
    }

    @Test
    void getSourceIsNeverNull() throws Exception {
        try (OrmLiteConnectionPool pool = pool()) {
            assertNotNull(pool.getSource());
        }
    }

    /** Regression: close() did not clear the flag, so isActive() stayed true forever. */
    @Test
    void isActiveReflectsTheClosedState() throws Exception {
        OrmLiteConnectionPool pool = pool();
        assertTrue(pool.isActive());
        pool.close();
        assertFalse(pool.isActive());
    }

    @Test
    void connectionSourceIsUsableByOrmLite() throws Exception {
        try (OrmLiteConnectionPool pool = pool()) {
            ConnectionSource source = pool.getSource();
            assertNotNull(source);
            TableUtils.createTable(source, Widget.class);
            Dao<Widget, String> dao = DaoManager.createDao(source, Widget.class);
            dao.create(new Widget("sprocket"));
            assertEquals(1, dao.countOf());
            assertEquals("sprocket", dao.queryForId("sprocket").name);
        }
    }

    @Test
    void constructorInvokesTheConfiguratorExactlyOnce() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        try (OrmLiteConnectionPool pool = new OrmLiteConnectionPool(
                H2Support.freshUrl(), H2Support.USER, H2Support.PASSWORD,
                source -> calls.incrementAndGet())) {
            assertEquals(1, calls.get());
            assertTrue(pool.isActive());
            assertNotNull(pool.getSource());
        }
    }

    /**
     * The configurator runs before initialize(), so a URL it installs is the one actually
     * used. Were the order reversed, this bogus URL would be ignored and construction
     * would quietly succeed.
     */
    @Test
    void constructorConfiguratorRunsBeforeInitialization() {
        assertThrows(Exception.class, () -> new OrmLiteConnectionPool(
                H2Support.freshUrl(), H2Support.USER, H2Support.PASSWORD,
                source -> source.setUrl("jdbc:bogus:nowhere")));
    }
}
