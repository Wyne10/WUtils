package me.wyne.wutils.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariConnectionPool implements ConnectionPool<HikariDataSource> {

    private final String url;
    private final String username;
    private final String password;

    private final HikariDataSource dataSource = new HikariDataSource();

    public HikariConnectionPool(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDataSource();
    }

    private void initializeDataSource() {
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setAutoCommit(false);
    }

    @Override
    public boolean isActive() {
        return dataSource.isRunning();
    }

    @Override
    public @Nullable Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LogWrapper.INSTANCE.exception("An exception occurred trying to establish connection with " + url, e);
        }
        return null;
    }

    @Override
    public @Nullable HikariDataSource getSource() {
        return dataSource;
    }

    @Override
    public void close() {
        dataSource.close();
    }

}
