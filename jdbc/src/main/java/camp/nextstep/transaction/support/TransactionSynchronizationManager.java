package camp.nextstep.transaction.support;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

public abstract class TransactionSynchronizationManager {

    private static final ThreadLocal<Map<DataSource, Connection>> resources = new ThreadLocal<>();

    private TransactionSynchronizationManager() {}

    public static boolean isTransactionActive(DataSource key) {
        Map<DataSource, Connection> dataSourceConnectionMap = resources.get();
        return dataSourceConnectionMap != null
                && dataSourceConnectionMap.get(key) != null;
    }

    public static Connection getResource(DataSource key) {
        Map<DataSource, Connection> dataSourceConnectionMap = resources.get();
        if (dataSourceConnectionMap == null) {
            return null;
        }

        return dataSourceConnectionMap.get(key);
    }

    public static void bindResource(DataSource key, Connection value) {
        Map<DataSource, Connection> dataSourceConnectionMap = resources.get();
        if (dataSourceConnectionMap == null) {
            Map<DataSource, Connection> newMap = new HashMap<>();
            newMap.put(key, value);
            resources.set(newMap);
            return;
        }

        dataSourceConnectionMap.put(key, value);
    }

    public static Connection unbindResource(DataSource key) {
        Map<DataSource, Connection> dataSourceConnectionMap = resources.get();
        if (dataSourceConnectionMap == null) {
            return null;
        }

        Connection connection = dataSourceConnectionMap.remove(key);
        if (dataSourceConnectionMap.isEmpty()) {
            resources.remove();
        }

        return connection;
    }
}
