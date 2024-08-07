package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.dao.QueryFormatException;
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

import static camp.nextstep.util.StringUtils.countContainSequence;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);
    private static final String QUERY_PLACEHOLDER = "?";

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        validateSql(sql, args);
        return executeQuery(sql, preparedStatement -> parse(rowMapper, preparedStatement), args);
    }

    public <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        validateSql(sql, args);
        return executeQuery(sql, preparedStatement -> parseToObject(rowMapper, preparedStatement), args);
    }

    public int update(String sql, Object... args) {
        validateSql(sql, args);
        return executeQuery(sql, PreparedStatement::executeUpdate, args);
    }

    private void validateSql(String sql, Object... args) {
        validateSqlBlank(sql);
        validateSqlArguments(sql, args);
    }

    private void validateSqlBlank(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new QueryFormatException("쿼리에는 문자열이 필수로 입력되어야합니다.");
        }
    }

    private void validateSqlArguments(String sql, Object... args) {
        if (countContainSequence(sql, QUERY_PLACEHOLDER) != args.length) {
            throw new QueryFormatException("sql의 placeholder 수에 맞는 파라미터가 필요합니다.");
        }
    }

    private <T> T executeQuery(String sql, PreparedStatementParser<T> preparedStatementParser, Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            prepareArguments(preparedStatement, args);

            return preparedStatementParser.parse(preparedStatement);
        } catch (SQLException e) {
            throw new DataAccessException(e);
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
            throw new DataAccessException(e);
        }
    }
}
