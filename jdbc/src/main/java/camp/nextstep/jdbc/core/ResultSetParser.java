package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetParser<T> {

    T parse(PreparedStatement preparedStatement) throws SQLException;
}
