package me.wyne.wutils.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * {@link ConnectionPool} backed by a {@link HikariDataSource}. Requires HikariCP as a
 * runtime dependency.
 */
public class HikariConnectionPool implements ConnectionPool<HikariDataSource> {

    private final String url;
    private final String username;
    private final String password;

    private final HikariDataSource dataSource = new HikariDataSource();

    public HikariConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password) {
        this(url, username, password, source -> {});
    }

    /**
     * Creates a pool whose underlying data source is handed to {@code configurator} before
     * it is put to use. This is the only point at which some settings can still be applied:
     * once the pool has started, its configuration is sealed.
     */
    public HikariConnectionPool(@NotNull String url, @NotNull String username, @NotNull String password,
                                @NotNull Consumer<@NotNull HikariDataSource> configurator) {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDataSource(configurator);
    }

    /**
     * Applies the URL and credentials this pool was constructed with to the data source.
     * Equivalent to {@link #initializeDataSource(Consumer)} with a configurator that does
     * nothing, leaving every other setting at HikariCP's default.
     */
    public void initializeDataSource() {
        initializeDataSource(source -> {});
    }

    /**
     * Applies the URL and credentials this pool was constructed with, then hands the data
     * source to {@code configurator} for any further setup — pool sizing, timeouts,
     * {@code autoCommit}, and so on.
     *
     * <p>Called with a no-op configurator by the constructor. HikariCP seals a data
     * source's configuration once the pool has started, so calling this again after the
     * first connection has been handed out throws {@link IllegalStateException}.
     */
    public void initializeDataSource(@NotNull Consumer<@NotNull HikariDataSource> configurator) {
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        configurator.accept(dataSource);
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
    public @NotNull HikariDataSource getSource() {
        return dataSource;
    }

    @Override
    public void close() {
        dataSource.close();
    }

}
