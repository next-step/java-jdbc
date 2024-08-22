package camp.nextstep.jdbc.datasource;

import camp.nextstep.transaction.support.TransactionStatus;
import java.sql.Connection;

public interface TransactionalManager {
    TransactionStatus getTransaction();
    void commit(TransactionStatus status);
    void rollback(TransactionStatus status);
    void doCleanUpAfterCompletion(Connection connection);
}
