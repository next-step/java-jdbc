package camp.nextstep.transaction.support;

import camp.nextstep.jdbc.core.JdbcException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class TransactionSynchronizationManager {

    private static final ThreadLocal<Map<DataSource, Connection>> resources = new ThreadLocal<>();

    private TransactionSynchronizationManager() {}

    public static Connection getResource(DataSource key) {
        final Map<DataSource, Connection> dataSourceConnectionMap = getOrCreateResourceMap();
        return dataSourceConnectionMap.get(key);
    }

    public static void bindResource(DataSource key, Connection value) {
        final Map<DataSource, Connection> dataSourceConnectionMap = getOrCreateResourceMap();
        final Connection oldConnection = dataSourceConnectionMap.put(key, value);

        if (oldConnection != null) {
            throw new JdbcException(
         					"Already connection [" + oldConnection + "] for key [" + key + "] bound to dataSourceConnectionMap");
        }
    }

    public static Connection unbindResource(DataSource key) {
        final Map<DataSource, Connection> dataSourceConnectionMap = getOrCreateResourceMap();
        final Connection connection = dataSourceConnectionMap.remove(key);

        if (connection == null) {
            throw new JdbcException("No value for key [" + key + "] bound to context");
        }

        return connection;
    }

    private static Map<DataSource, Connection> getOrCreateResourceMap() {
        Map<DataSource, Connection> dataSourceConnectionMap = resources.get();

        if (dataSourceConnectionMap == null) {
            dataSourceConnectionMap = new HashMap<>();
            resources.set(dataSourceConnectionMap);
        }

        return dataSourceConnectionMap;
    }
}
