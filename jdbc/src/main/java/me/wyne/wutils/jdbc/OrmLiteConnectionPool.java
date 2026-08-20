package me.wyne.wutils.jdbc;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * {@link ConnectionPool} backed by ORMLite's {@link JdbcPooledConnectionSource}. Requires
 * ORMLite as a runtime dependency.
 *
 * <p>{@link #getConnection()} is not supported by this implementation: ORMLite manages
 * {@link Connection} instances internally through its own {@link ConnectionSource} rather
 * than exposing them, so calling it always throws instead of returning or throwing
 * {@link SQLException}. Use {@link #getSource()} to obtain the {@link ConnectionSource}
 * instead, or use {@link HikariConnectionPool} if raw JDBC connections are required.
 */
public class OrmLiteConnectionPool implements ConnectionPool<ConnectionSource> {

    private final String url;
    private final String username;
    private final String password;

    private final JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource();
    private boolean isInitialized = false;

    public OrmLiteConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password) throws SQLException {
        this(url, username, password, source -> {});
    }

    /**
     * Creates a pool whose underlying data source is handed to {@code configurator} before
     * it is put to use. This is the only point at which some settings can still be applied:
     * once the pool has started, its configuration is sealed.
     */
    public OrmLiteConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password,
                                 @NotNull Consumer<@NotNull JdbcPooledConnectionSource> configurator) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDataSource(configurator);
    }

    /**
     * Applies the URL and credentials this pool was constructed with and initializes the
     * connection source. Equivalent to {@link #initializeDataSource(Consumer)} with a
     * configurator that does nothing.
     */
    public void initializeDataSource() throws SQLException {
        initializeDataSource(source -> {});
    }

    /**
     * Applies the URL and credentials this pool was constructed with, hands the connection
     * source to {@code configurator} for any further setup, then initializes it.
     *
     * <p>The configurator runs before initialization, so anything it sets is in effect for
     * the pool that initialization builds. Called with a no-op configurator by the
     * constructor.
     */
    public void initializeDataSource(@NotNull Consumer<@NotNull JdbcPooledConnectionSource> configurator) throws SQLException {
        connectionSource.setUrl(url);
        connectionSource.setUsername(username);
        connectionSource.setPassword(password);
        configurator.accept(connectionSource);
        connectionSource.initialize();
        isInitialized = true;
    }

    @Override
    public boolean isActive() {
        return isInitialized;
    }

    /**
     * Always throws: this implementation does not support handing out raw {@link Connection}
     * instances. See the class documentation.
     */
    @Override
    public @NotNull Connection getConnection() {
        throw new UnsupportedOperationException("OrmLiteConnectionPool doesn't provide java.sql connections");
    }

    @Override
    public @NotNull ConnectionSource getSource() {
        return connectionSource;
    }

    @Override
    public void close() throws Exception {
        try {
            connectionSource.close();
        } finally {
            isInitialized = false;
        }
    }

}
