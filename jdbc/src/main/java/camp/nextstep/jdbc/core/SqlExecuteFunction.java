package camp.nextstep.jdbc.core;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlExecuteFunction<T> {
    void run(T t) throws SQLException;
}
