package br.com.sisprof.m4jruntime.database;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by kaoe on 14/09/16.
 */
public class PostgresqlDatabaseFactory implements DatabaseFactory {

    private final DataSource dataSource;

    private PostgresqlDatabaseFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static PostgresqlDatabaseFactory newFactory(DataSource dataSource) {
        return new PostgresqlDatabaseFactory(dataSource);
    }

    @Override
    public DatabaseStorage create() {
        try {
            return new PostgresqlDatabaseStorage(dataSource.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
    }
}
