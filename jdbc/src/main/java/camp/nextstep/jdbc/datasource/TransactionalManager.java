package camp.nextstep.jdbc.datasource;

import camp.nextstep.transaction.support.TransactionStatus;

public interface TransactionalManager {
    TransactionStatus getTransaction();
    void commit(TransactionStatus status);
    void rollback(TransactionStatus status);
    void doCleanUpAfterCompletion();
}
