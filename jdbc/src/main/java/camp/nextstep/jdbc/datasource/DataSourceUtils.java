package camp.nextstep.jdbc.datasource;

import camp.nextstep.jdbc.CannotGetJdbcConnectionException;
import camp.nextstep.jdbc.core.DataAccessException;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataSourceUtils {

    private DataSourceUtils() {}

    public static Connection getConnection(final DataSource dataSource) throws CannotGetJdbcConnectionException {
        final Connection connection = TransactionSynchronizationManager.getResource(dataSource);

        if (connection != null) {
            return connection;
        }

        try {
            return dataSource.getConnection();
        } catch (final SQLException ex) {
            throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
        }
    }

    public static void releaseConnection(final Connection connection, final DataSource dataSource) {
        final Connection currnetContextConnection = TransactionSynchronizationManager.getResource(dataSource);
        if (currnetContextConnection != null) {
            return;
        }

        try {
            connection.close();
        } catch (final SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
