package me.wyne.wutils.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DriverShimTest {

    /** Records what it was called with and returns recognizable values. */
    private static final class RecordingDriver implements Driver {
        String url;
        Properties info;
        boolean acceptsUrlResult = true;
        final DriverPropertyInfo[] propertyInfo = new DriverPropertyInfo[]{
                new DriverPropertyInfo("name", "value")};
        final Logger logger = Logger.getLogger("recording-driver");

        @Override
        public Connection connect(String url, Properties info) {
            this.url = url;
            this.info = info;
            return null;
        }

        @Override
        public boolean acceptsURL(String url) {
            this.url = url;
            return acceptsUrlResult;
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
            this.url = url;
            this.info = info;
            return propertyInfo;
        }

        @Override
        public int getMajorVersion() {
            return 7;
        }

        @Override
        public int getMinorVersion() {
            return 3;
        }

        @Override
        public boolean jdbcCompliant() {
            return true;
        }

        @Override
        public Logger getParentLogger() {
            return logger;
        }
    }

    @Test
    void forwardsEveryCallToTheWrappedDriver() throws Exception {
        RecordingDriver wrapped = new RecordingDriver();
        DriverShim shim = new DriverShim(wrapped);
        Properties info = new Properties();

        assertNull(shim.connect("jdbc:test:one", info));
        assertEquals("jdbc:test:one", wrapped.url);
        assertSame(info, wrapped.info);

        assertTrue(shim.acceptsURL("jdbc:test:two"));
        assertEquals("jdbc:test:two", wrapped.url);

        assertSame(wrapped.propertyInfo, shim.getPropertyInfo("jdbc:test:three", info));
        assertEquals(7, shim.getMajorVersion());
        assertEquals(3, shim.getMinorVersion());
        assertTrue(shim.jdbcCompliant());
        assertSame(wrapped.logger, shim.getParentLogger());
    }

    @Test
    void passesThroughARejectedUrl() throws Exception {
        RecordingDriver wrapped = new RecordingDriver();
        wrapped.acceptsUrlResult = false;
        assertFalse(new DriverShim(wrapped).acceptsURL("jdbc:other:thing"));
    }

    /** A null Properties is legal at the JDBC layer and must reach the wrapped driver. */
    @Test
    void forwardsANullPropertiesUnchanged() throws Exception {
        RecordingDriver wrapped = new RecordingDriver();
        assertNull(new DriverShim(wrapped).connect("jdbc:test:four", null));
        assertNull(wrapped.info);
    }
}
