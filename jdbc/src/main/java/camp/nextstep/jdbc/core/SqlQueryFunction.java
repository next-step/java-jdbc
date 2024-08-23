package camp.nextstep.jdbc.core;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlQueryFunction<T, R> {
    R apply(T t) throws SQLException;
}
