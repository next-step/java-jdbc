package camp.nextstep.transaction;

public interface TransactionManager {

    void begin();

    void commit();

    void rollback();

    void close();
}
