package me.wyne.wutils.jdbc;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * {@link ConnectionPool} that pools connections through a {@link HikariDataSource} while
 * exposing an ORMLite {@link ConnectionSource} via {@link #getSource()}, bridging the two
 * with {@link DataSourceConnectionSource}. Requires both HikariCP and ORMLite as runtime
 * dependencies.
 */
public class HikariOrmLiteConnectionPool implements ConnectionPool<ConnectionSource> {

    private final String url;
    private final String username;
    private final String password;

    private final HikariDataSource dataSource = new HikariDataSource();
    private ConnectionSource connectionSource;

    public HikariOrmLiteConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password) throws SQLException {
        this(url, username, password, source -> {});
    }

    /**
     * Creates a pool whose underlying data source is handed to {@code configurator} before
     * it is put to use. This is the only point at which some settings can still be applied:
     * once the pool has started, its configuration is sealed.
     */
    public HikariOrmLiteConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password,
                                       @NotNull Consumer<@NotNull HikariDataSource> configurator) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDataSource(configurator);
    }

    /**
     * Applies the URL and credentials this pool was constructed with to the Hikari data
     * source and builds the ORMLite {@link ConnectionSource} over it. Equivalent to
     * {@link #initializeDataSource(Consumer)} with a configurator that does nothing.
     */
    public void initializeDataSource() throws SQLException {
        initializeDataSource(source -> {});
    }

    /**
     * Applies the URL and credentials this pool was constructed with, hands the Hikari data
     * source to {@code configurator} for any further setup, then builds the ORMLite
     * {@link ConnectionSource} over the configured data source.
     *
     * <p>The configurator runs before the {@link ConnectionSource} is created, so settings
     * it applies are in effect for every connection ORMLite takes. Called with a no-op
     * configurator by the constructor.
     */
    public void initializeDataSource(@NotNull Consumer<@NotNull HikariDataSource> configurator) throws SQLException {
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        configurator.accept(dataSource);
        connectionSource = new DataSourceConnectionSource(dataSource, url);
    }

    @Override
    public boolean isActive() {
        return dataSource.isRunning();
    }

    @Override
    public @NotNull Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public @NotNull ConnectionSource getSource() {
        return connectionSource;
    }

    @Override
    public void close() throws Exception {
        connectionSource.close();
        dataSource.close();
    }

}
