package camp.nextstep.transaction;

import camp.nextstep.jdbc.CannotCloseJdbcConnectionException;
import camp.nextstep.jdbc.CannotGetJdbcConnectionException;
import camp.nextstep.jdbc.datasource.DataSourceUtils;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class TransactionHandler {
    public void begin(DataSource dataSource) {
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new CannotGetJdbcConnectionException("JDBC 커넥션 획득 실패", e);
        }
    }

    public void commit(DataSource dataSource) {
        Connection connection = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            connection.commit();
        } catch (SQLException e) {
            throw new CannotCommitException("커밋 실패", e);
        } finally {
            TransactionSynchronizationManager.unbindResource(dataSource);
            closeConnection(connection);
        }
    }

    public void rollback(DataSource dataSource) {
        Connection connection = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            connection.rollback();
        } catch (SQLException e) {
            throw new CannotRollbackException("롤백 실패", e);
        } finally {
            TransactionSynchronizationManager.unbindResource(dataSource);
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new CannotCloseJdbcConnectionException("Connection close 실패");
            }
        }
    }
}
