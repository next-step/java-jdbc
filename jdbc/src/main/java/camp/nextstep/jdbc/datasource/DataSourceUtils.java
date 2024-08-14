package camp.nextstep.jdbc.datasource;

import camp.nextstep.jdbc.CannotGetJdbcConnectionException;
import camp.nextstep.jdbc.transaction.TransactionSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataSourceUtils {

    private static final Logger log = LoggerFactory.getLogger(DataSourceUtils.class);

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
            logSQLException(ex);
            throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
        }
    }

    public static void releaseConnection(DataSource dataSource) {
        try {
            TransactionSynchronizationManager.unbindResource(dataSource);
        } catch (SQLException ex) {
            logSQLException(ex);
            throw new CannotGetJdbcConnectionException("Failed to close JDBC Connection");
        }
    }

    private static void logSQLException(SQLException e) {
        log.error("ERROR {} ({}) : {}", e.getErrorCode(), e.getSQLState(), e.getMessage());
    }
}
