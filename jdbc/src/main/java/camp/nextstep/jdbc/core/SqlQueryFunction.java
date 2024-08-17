package camp.nextstep.jdbc.core;

import java.sql.SQLException;

public interface SqlQueryFunction<T, R> {
    R apply(T t) throws SQLException;
}
