package camp.nextstep.jdbc.core;

import java.sql.Connection;

public interface PreparedStatementSetter {

    <T> T executeQuery(String sql, PreparedStatementParser<T> preparedStatementParser, Object... args);

    <T> T executeQuery(Connection connection, String sql, PreparedStatementParser<T> preparedStatementParser, Object... args);
}
