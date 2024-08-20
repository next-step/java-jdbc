package camp.nextstep.jdbc.core;

import java.sql.SQLException;

public interface SqlExecuteFunction<T> {
    void run(T t) throws SQLException;
}
