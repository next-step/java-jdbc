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
        return executeQuery(sql, resultSet -> parse(rowMapper, resultSet), args);
    }

    public <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        return executeQuery(sql, resultSet -> parseToObject(rowMapper, resultSet), args);
    }

    public int update(String sql, Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            prepareArguments(preparedStatement, args);

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T executeQuery(String sql, ResultSetParser<T> resultSetParser, Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            prepareArguments(preparedStatement, args);

            return convertResultSet(preparedStatement, resultSetParser);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareArguments(PreparedStatement preparedStatement, Object... args) throws SQLException {
        for (int i = 1; i <= args.length; i++) {
            preparedStatement.setObject(i, args[i - 1]);
        }
    }

    private <T> T convertResultSet(PreparedStatement preparedStatement, ResultSetParser<T> resultSetParser) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSetParser.parse(resultSet);
        }
    }

    private <T> List<T> parse(RowMapper<T> rowMapper, ResultSet resultSet) throws SQLException {
        List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(rowMapper.mapRow(resultSet));
        }
        return result;
    }

    private <T> Optional<T> parseToObject(RowMapper<T> rowMapper, ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(rowMapper.mapRow(resultSet));
        }
        return Optional.empty();
    }
}
