package camp.nextstep.jdbc.transaction;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.jdbc.datasource.DataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    private static final Logger log = LoggerFactory.getLogger(TransactionManager.class);

    private final DataSource dataSource;

    public TransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void getTransaction() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        executeSqlRunner(() -> connection.setAutoCommit(false));
    }

    public void commit() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        executeSqlRunnerAndRelease(connection::commit);
    }

    public void rollback() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        executeSqlRunnerAndRelease(connection::rollback);
    }

    private void executeSqlRunner(SqlRunnable sqlRunnable) {
        try {
            sqlRunnable.run();
        } catch (SQLException e) {
            log.error("ERROR {} ({}) : {}", e.getErrorCode(), e.getSQLState(), e.getMessage());
            throw new DataAccessException(e);
        }
    }

    private void executeSqlRunnerAndRelease(SqlRunnable sqlRunnable) {
        executeSqlRunner(sqlRunnable);
        DataSourceUtils.releaseConnection(dataSource);
    }
}
