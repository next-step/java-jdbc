package camp.nextstep.transaction;

import camp.nextstep.jdbc.datasource.DataSourceUtils;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DataSourceTransactionManager implements TransactionManager {

    private final DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void begin() {
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        DataSourceUtils.releaseConnection(dataSource);
    }
}
