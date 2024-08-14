package camp.nextstep.transaction;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.jdbc.datasource.ConnectionUtils;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class TransactionHandler {
    public void executeWithTransaction(DataSource dataSource, TransactionCallback txCallback) {
        Connection connection = null;
        try {
            connection = ConnectionUtils.getConnection(dataSource);
            connection.setAutoCommit(false);

            txCallback.execute();

            connection.commit();
        } catch (SQLException | DataAccessException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                throw e;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            try {
                TransactionSynchronizationManager.unbindResource(dataSource);
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {}
        }
    }
}
