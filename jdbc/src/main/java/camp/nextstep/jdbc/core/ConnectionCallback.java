package camp.nextstep.jdbc.core;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionCallback<T> {
    T accept(final Connection connection) throws SQLException;
}
