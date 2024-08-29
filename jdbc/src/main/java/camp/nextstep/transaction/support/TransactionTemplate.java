package camp.nextstep.transaction.support;

public class TransactionTemplate {
    private final TransactionManager transactionManager;

    public TransactionTemplate(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public <T> T execute(TransactionCallback<T> action) {
        TransactionStatus status = transactionManager.getTransaction();

        try {
            T result = action.doInTransaction(status);
            transactionManager.commit(status);
            return result;
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
