package camp.nextstep.transaction.support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public abstract class TransactionSynchronizationManager {

    private static final ThreadLocal<Map<DataSource, ConnectionHolder>> resources = ThreadLocal.withInitial(HashMap::new);

    private TransactionSynchronizationManager() {
    }

    public static ConnectionHolder getResource(final DataSource key) {
        final Map<DataSource, ConnectionHolder> dataSourceConnectionMap = resources.get();

        return dataSourceConnectionMap.get(key);
    }

    public static void bindResource(final DataSource key, final Connection value) {
        final Map<DataSource, ConnectionHolder> dataSourceConnectionMap = resources.get();

        if (dataSourceConnectionMap.containsKey(key)) {
            throw new IllegalStateException("Resource is already bound to key " + key);
        }

        dataSourceConnectionMap.put(key, new ConnectionHolder(value));
    }

    public static ConnectionHolder unbindResource(final DataSource key) {
        return resources.get().remove(key);
    }
}
