package me.wyne.wutils.jdbc;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariOrmLiteConnectionPool implements ConnectionPool<ConnectionSource> {

    private final String url;
    private final String username;
    private final String password;

    private final HikariDataSource dataSource = new HikariDataSource();
    private ConnectionSource connectionSource;

    public HikariOrmLiteConnectionPool(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDataSource();
    }

    private void initializeDataSource() throws SQLException {
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        connectionSource = new DataSourceConnectionSource(dataSource, url);
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
    public @Nullable ConnectionSource getSource() {
        return connectionSource;
    }

    @Override
    public void close() throws Exception {
        connectionSource.close();
        dataSource.close();
    }

}
