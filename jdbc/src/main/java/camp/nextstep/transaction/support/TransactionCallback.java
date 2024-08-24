package camp.nextstep.transaction.support;

public interface TransactionCallback<T> {
    T doInTransaction(TransactionStatus status);
}
