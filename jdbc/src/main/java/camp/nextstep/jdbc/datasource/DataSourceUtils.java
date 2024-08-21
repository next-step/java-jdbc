package camp.nextstep.jdbc.datasource;

import camp.nextstep.jdbc.CannotGetJdbcConnectionException;
import camp.nextstep.transaction.support.ConnectionHolder;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataSourceUtils {

    private DataSourceUtils() {
    }

    public static Connection getConnection(final DataSource dataSource) throws CannotGetJdbcConnectionException {
        final ConnectionHolder connectionHolder = TransactionSynchronizationManager.getResource(dataSource);

        if (connectionHolder != null) {
            connectionHolder.requested();
            return connectionHolder.getConnection();
        }

        try {
            final Connection connection = dataSource.getConnection();
            TransactionSynchronizationManager.bindResource(dataSource, connection);
            return connection;
        } catch (final SQLException ex) {
            throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
        }
    }

    public static void releaseConnection(final Connection connection, final DataSource dataSource) {
        final ConnectionHolder connectionHolder = TransactionSynchronizationManager.getResource(dataSource);

        if (connectionHolder == null) {
            closeConnection(connection);
            return;
        }

        connectionHolder.released();

        if (connectionHolder.isOpen()) {
            return;
        }
        TransactionSynchronizationManager.unbindResource(dataSource);
        closeConnection(connection);
    }

    private static void closeConnection(final Connection connection) {
        try {
            connection.close();
        } catch (final SQLException e) {
            throw new CannotGetJdbcConnectionException("Failed to close JDBC Connection");
        }
    }
}
