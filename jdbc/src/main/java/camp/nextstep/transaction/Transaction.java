package camp.nextstep.transaction;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.jdbc.datasource.DataSourceUtils;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class Transaction implements AutoCloseable {

  private final DataSource dataSource;
  private boolean committed = false;

  public Transaction(DataSource dataSource) {
    this.dataSource = dataSource;

    try {
      Connection connection = DataSourceUtils.getConnection(dataSource);
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      throw new DataAccessException("Failed to set auto commit to false", e);
    }
  }

  public void commit() {
    try {
      Connection connection = DataSourceUtils.getConnection(dataSource);
      connection.commit();
      committed = true;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to commit transaction", e);
    }
  }

  public void rollback() {
    try {
      Connection connection = DataSourceUtils.getConnection(dataSource);
      connection.rollback();
    } catch (SQLException e) {
      throw new DataAccessException("Failed to rollback transaction", e);
    }
  }

  @Override
  public void close() {
    try {
      if (!committed) {
        rollback();  // If not committed, rollback on close
      }
    } finally {
      DataSourceUtils.releaseConnection(dataSource);
    }
  }
}
