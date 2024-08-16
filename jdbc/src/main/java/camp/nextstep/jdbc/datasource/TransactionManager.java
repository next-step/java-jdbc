package camp.nextstep.jdbc.datasource;

public interface TransactionManager {

    void begin();
    void commit();
    void rollback();
    void close();
}
