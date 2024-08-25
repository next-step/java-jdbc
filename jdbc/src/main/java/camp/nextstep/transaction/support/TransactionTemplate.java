package camp.nextstep.transaction.support;

/**
 * 트랜잭션을 처리하는 템플릿 클래스
 * <p>
 * 한 트랜잭션 내에서 TransactionCallback 을 실행할 수 있도록 지원한다.실패할 경우 롤백을 실행한다.
 */
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
