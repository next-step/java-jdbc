package camp.nextstep.jdbc.datasource;

import java.sql.Connection;
import javax.sql.DataSource;

public class DataSourceTransactionManager implements TransactionManager {

    private DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void begin() {
        try {
            DataSourceUtils.getConnection(dataSource).setAutoCommit(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            DataSourceUtils.getConnection(dataSource).commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            DataSourceUtils.getConnection(dataSource).rollback();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            connection.setAutoCommit(true);
            DataSourceUtils.releaseConnection(connection, dataSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
