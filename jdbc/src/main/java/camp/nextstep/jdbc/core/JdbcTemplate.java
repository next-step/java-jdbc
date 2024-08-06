package camp.nextstep.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        return executeQuery(sql, preparedStatement -> parse(rowMapper, preparedStatement), args);
    }

    public <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        return executeQuery(sql, preparedStatement -> parseToObject(rowMapper, preparedStatement), args);
    }

    public int update(String sql, Object... args) {
        return executeQuery(sql, PreparedStatement::executeUpdate, args);
    }

    private <T> T executeQuery(String sql, PreparedStatementParser<T> preparedStatementParser, Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            prepareArguments(preparedStatement, args);

            return preparedStatementParser.parse(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareArguments(PreparedStatement preparedStatement, Object... args) throws SQLException {
        for (int i = 1; i <= args.length; i++) {
            preparedStatement.setObject(i, args[i - 1]);
        }
    }

    private <T> List<T> parse(RowMapper<T> rowMapper, PreparedStatement preparedStatement) {
        return parseResultSet(parse(rowMapper), preparedStatement);
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

    private <T> Optional<T> parseToObject(RowMapper<T> rowMapper, PreparedStatement preparedStatement) {
        return parseResultSet(parseToObject(rowMapper), preparedStatement);
    }

    private <T> ResultSetParser<Optional<T>> parseToObject(RowMapper<T> rowMapper) {
        return resultSet -> {
            if (resultSet.next()) {
                return Optional.of(rowMapper.mapRow(resultSet));
            }
            return Optional.empty();
        };
    }

    private <T> T parseResultSet(ResultSetParser<T> resultSetParser, PreparedStatement preparedStatement) {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSetParser.parse(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
