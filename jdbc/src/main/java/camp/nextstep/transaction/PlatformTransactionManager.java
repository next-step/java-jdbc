package camp.nextstep.transaction;

public interface PlatformTransactionManager {
    void getTransaction();

    void commit();

    void rollback();
}
