package camp.nextstep.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetParser<T> {

    T parse(ResultSet resultSet) throws SQLException;
}
