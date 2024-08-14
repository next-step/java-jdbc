package camp.nextstep.jdbc.datasource;

import camp.nextstep.jdbc.CannotCloseJdbcConnectionException;
import camp.nextstep.jdbc.CannotGetJdbcConnectionException;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public abstract class ConnectionUtils {
    private ConnectionUtils() {
    }

    public static Connection getConnection(DataSource dataSource) {
        if (TransactionSynchronizationManager.isTransactionActive(dataSource)) {
            return TransactionSynchronizationManager.getResource(dataSource);
        }

        try {
            Connection connection = dataSource.getConnection();
            TransactionSynchronizationManager.bindResource(dataSource, connection);
            return connection;
        } catch (SQLException e) {
            throw new CannotGetJdbcConnectionException("JDBC 커넥션 획득 실패");
        }
    }

    public static void closeConnection(Connection connection, DataSource dataSource) {
        if (connection != null && !TransactionSynchronizationManager.isTransactionActive(dataSource)) {
            close(connection);
        }
    }

    private static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new CannotCloseJdbcConnectionException("JDBC Connection Release 실패");
        }
    }

}
