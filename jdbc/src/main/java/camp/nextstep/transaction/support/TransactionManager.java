package camp.nextstep.transaction.support;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private final DataSource dataSource;

    public TransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public TransactionStatus getTransaction() {
        try {
            final Connection connection = DataSourceUtils.getConnection(dataSource);
            connection.setAutoCommit(false);
            return new TransactionStatus(connection);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void commit(TransactionStatus status) {
        Connection connection = status.connection();

        status.checkConnectionActive();
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    public void rollback(TransactionStatus status) {
        Connection connection = status.connection();

        status.checkConnectionActive();
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

}
