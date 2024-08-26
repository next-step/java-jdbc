package camp.nextstep.jdbc.datasource;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.transaction.support.DefaultTransactionStatus;
import camp.nextstep.transaction.support.TransactionStatus;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class PlatformTransactionalManager implements TransactionalManager {


    @Override
    public TransactionStatus getTransaction(DataSource dataSource) {
        try {
            DataSourceUtils
                .getConnection(dataSource)
                .setAutoCommit(false);
            return new DefaultTransactionStatus();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void commit(TransactionStatus transactionStatus, DataSource dataSource) {
        try {
            Connection connection = DataSourceUtils
                .getConnection(dataSource);
            connection.commit();

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void rollback(TransactionStatus transactionStatus, DataSource dataSource) {
        try {
            Connection connection = DataSourceUtils
                .getConnection(dataSource);
            connection.rollback();

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void doCleanUpAfterCompletion(DataSource dataSource) {

        Connection connection = DataSourceUtils
            .getConnection(dataSource);
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        TransactionSynchronizationManager.unbindResource(dataSource);

    }

}
