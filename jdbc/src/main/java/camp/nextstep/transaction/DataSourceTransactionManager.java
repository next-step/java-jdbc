package camp.nextstep.transaction;

import javax.sql.DataSource;

public class DataSourceTransactionManager implements TransactionManager {

  private final DataSource dataSource;

  public DataSourceTransactionManager(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Transaction begin() {
    return new Transaction(dataSource);
  }
}
