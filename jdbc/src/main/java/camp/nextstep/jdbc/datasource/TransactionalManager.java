package camp.nextstep.jdbc.datasource;

import camp.nextstep.transaction.support.TransactionStatus;
import java.sql.Connection;
import javax.sql.DataSource;

public interface TransactionalManager {
    TransactionStatus getTransaction(DataSource dataSource);
    void commit(TransactionStatus transactionStatus, DataSource dataSource);
    void rollback(TransactionStatus transactionStatus, DataSource dataSource);
    void doCleanUpAfterCompletion(DataSource dataSource);
}
