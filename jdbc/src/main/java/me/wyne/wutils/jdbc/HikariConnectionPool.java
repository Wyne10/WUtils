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
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
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
