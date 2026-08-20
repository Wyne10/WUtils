package me.wyne.wutils.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Delegates every {@link Driver} call to a wrapped driver instance.
 *
 * <p>{@link DriverManager} only accepts a driver from a caller if the driver's class,
 * resolved through that caller's classloader, is the exact same class as the registered
 * instance. A driver loaded at runtime through a private {@link java.net.URLClassLoader}
 * (see {@link DriverLibrary}) fails that check for any caller outside that classloader and
 * becomes effectively invisible to {@code DriverManager.getConnection}. Wrapping the loaded
 * driver in a shim class that is itself loaded by this plugin's own classloader satisfies
 * the check, while every call is simply forwarded to the real driver underneath.
 *
 * <p>The shim imposes no constraints of its own: parameter nullability below mirrors what
 * {@link DriverManager} forwards, and every result is the wrapped driver's.</p>
 */
public class DriverShim implements Driver {

    private final Driver driver;

    public DriverShim(@NotNull Driver driver) {
        this.driver = driver;
    }

    @Override
    public @Nullable Connection connect(@NotNull String url, @Nullable Properties info) throws SQLException {
        return driver.connect(url, info);
    }

    @Override
    public boolean acceptsURL(@NotNull String url) throws SQLException {
        return driver.acceptsURL(url);
    }

    @Override
    public @NotNull DriverPropertyInfo[] getPropertyInfo(@NotNull String url, @Nullable Properties info) throws SQLException {
        return driver.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
        return driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return driver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return driver.jdbcCompliant();
    }

    @Override
    public @NotNull Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return driver.getParentLogger();
    }

}
