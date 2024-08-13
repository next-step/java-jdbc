package camp.nextstep.transaction.support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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

    public static Connection bindResource(DataSource key, Connection value) {
        Map<DataSource, Connection> currentResources = resources.get();
        if (currentResources.containsKey(key)) {
            throw new IllegalArgumentException("이미 바인딩된 DataSource입니다.");
        }
        currentResources.put(key, value);
        return value;
    }

    public static void unbindResource(DataSource key) throws SQLException {
        Map<DataSource, Connection> currentResources = resources.get();
        if (!currentResources.containsKey(key)) {
            throw new IllegalArgumentException("key에 해당하는 바인딩된 DataSource가 없습니다.");
        }
        Connection connection = currentResources.remove(key);
        connection.close();
    }
}
