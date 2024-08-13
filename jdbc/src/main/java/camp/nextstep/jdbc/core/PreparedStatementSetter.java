package camp.nextstep.jdbc.core;

public interface PreparedStatementSetter {

    <T> T executeQuery(String sql, PreparedStatementParser<T> preparedStatementParser, Object... args);
}
