package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultResultSetSetter implements ResultSetSetter {

    @Override
    public <T> List<T> parse(RowMapper<T> rowMapper, PreparedStatement preparedStatement) throws SQLException {
        return parseResultSet(parse(rowMapper), preparedStatement);
    }

    @Override
    public <T> Optional<T> parseToObject(RowMapper<T> rowMapper, PreparedStatement preparedStatement) throws SQLException {
        return parseResultSet(parseToObject(rowMapper), preparedStatement);
    }

    private <T> T parseResultSet(ResultSetParser<T> resultSetParser, PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSetParser.parse(resultSet);
        }
    }

    private <T> ResultSetParser<List<T>> parse(RowMapper<T> rowMapper) {
        return resultSet -> {
            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        };
    }

    private <T> ResultSetParser<Optional<T>> parseToObject(RowMapper<T> rowMapper) {
        return resultSet -> {
            if (resultSet.next()) {
                return Optional.of(rowMapper.mapRow(resultSet));
            }
            return Optional.empty();
        };
    }
}
