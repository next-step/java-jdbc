package camp.nextstep.transaction.support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

public abstract class TransactionSynchronizationManager {

    private static final ThreadLocal<Map<DataSource, Connection>> resources = new ThreadLocal<>();

    private TransactionSynchronizationManager() {}

    public static boolean isTransactionActive(DataSource key) {
        Map<DataSource, Connection> dataSourceConnectionMap = resources.get();
        return dataSourceConnectionMap != null && dataSourceConnectionMap.get(key) != null;
    }

    public static Connection getResource(DataSource key) {
        Map<DataSource, Connection> dataSourceConnectionMap = resources.get();
        return dataSourceConnectionMap == null ? null : dataSourceConnectionMap.get(key);
    }

    public static void bindResource(DataSource key, Connection value) {
        resources.set(Map.of(key, value));
    }

    public static Connection unbindResource(DataSource key) {
        Map<DataSource, Connection> dataSourceConnectionMap = resources.get();
        resources.remove();
        return dataSourceConnectionMap == null ? null : dataSourceConnectionMap.get(key);
    }
}
