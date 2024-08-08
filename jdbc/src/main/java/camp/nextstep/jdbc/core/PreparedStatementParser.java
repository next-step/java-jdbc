package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementParser<T> {

    T parse(PreparedStatement preparedStatement) throws SQLException;
}
