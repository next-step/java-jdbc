package camp.nextstep.transaction.support;

import camp.nextstep.transaction.PlatformTransactionManager;

import java.util.function.Supplier;

public class TransactionTemplate {

    private final PlatformTransactionManager transactionManager;

    public TransactionTemplate(final PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void run(final Runnable runnable) {
        runInternal(() -> {
            runnable.run();
            return null;
        });
    }

    public <T> T run(final Supplier<T> supplier) {
        return runInternal(supplier);
    }

    private <T> T runInternal(final Supplier<T> supplier) {
        try {
            transactionManager.getTransaction();
            final T result = supplier.get();
            transactionManager.commit();
            return result;
        } catch (final RuntimeException e) {
            transactionManager.rollback();
            throw e;
        } catch (final Exception e) {
            transactionManager.rollback();
            throw new TransactionException("transaction fail to start", e);
        }
    }

}
