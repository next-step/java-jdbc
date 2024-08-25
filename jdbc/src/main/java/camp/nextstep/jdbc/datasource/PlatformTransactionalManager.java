package camp.nextstep.jdbc.datasource;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.transaction.support.TransactionStatus;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class PlatformTransactionalManager implements TransactionalManager {

    private final DataSource dataSource;

    public PlatformTransactionalManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TransactionStatus getTransaction() {
        try {
            DataSourceUtils
                .getConnection(dataSource)
                .setAutoCommit(false);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return null;
    }

    @Override
    public void commit(TransactionStatus status) {
        try {
            Connection connection = DataSourceUtils
                .getConnection(dataSource);
            connection.rollback();

            doCleanUpAfterCompletion(connection);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void rollback(TransactionStatus status) {
        try {
            Connection connection = DataSourceUtils
                .getConnection(dataSource);
            connection.rollback();

            doCleanUpAfterCompletion(connection);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void doCleanUpAfterCompletion(Connection connection) {
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

}
