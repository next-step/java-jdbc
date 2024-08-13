package camp.nextstep.transaction.support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TransactionSynchronizationManager {

    private static final ThreadLocal<Map<DataSource, Connection>> resources = new ThreadLocal<>() {
        @Override
        protected Map<DataSource, Connection> initialValue() {
            return new HashMap<>();
        }
    };

    private TransactionSynchronizationManager() {}

    public static Optional<Connection> findResource(DataSource key) {
        Map<DataSource, Connection> currentResources = resources.get();
        return Optional.ofNullable(currentResources.get(key));
    }

    public static void bindResource(DataSource key, Connection value) {
    }

    public static Connection unbindResource(DataSource key) {
        return null;
    }
}
