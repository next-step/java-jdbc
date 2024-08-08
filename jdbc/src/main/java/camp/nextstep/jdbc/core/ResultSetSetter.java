package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ResultSetSetter {

    <T> List<T> parse(RowMapper<T> rowMapper, PreparedStatement preparedStatement) throws SQLException;

    <T> Optional<T> parseToObject(RowMapper<T> rowMapper, PreparedStatement preparedStatement) throws SQLException;
}
