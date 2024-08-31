package camp.nextstep.jdbc.core;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultSetHandler<T> {
    T handle(ResultSet resultSet);
}