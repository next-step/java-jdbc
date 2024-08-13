package camp.nextstep.jdbc.datasource;

import camp.nextstep.jdbc.CannotGetJdbcConnectionException;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataSourceUtils {

    private DataSourceUtils() {}

    public static Connection getConnection(DataSource dataSource) throws CannotGetJdbcConnectionException {
        return TransactionSynchronizationManager.findResource(dataSource)
                .orElseGet(() -> bindConnection(dataSource));
    }

    private static Connection bindConnection(DataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            return TransactionSynchronizationManager.bindResource(dataSource, connection);
        } catch (SQLException ex) {
            throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
        }
    }

    public static void releaseConnection(Connection connection, DataSource dataSource) {
        try {
            connection.close();
        } catch (SQLException ex) {
            throw new CannotGetJdbcConnectionException("Failed to close JDBC Connection");
        }
    }
}
