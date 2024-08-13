package camp.nextstep.jdbc.core;

import camp.nextstep.jdbc.datasource.DataSourceUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        final List<T> results = queryForList(sql, rowMapper, args);
        if (results.size() != 1) {
            throw new RuntimeException("조회 결과가 1개가 아닙니다.");
        }
        return results.get(0);
    }

    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) {
        try (final PreparedStatement preparedStatement = DataSourceUtils.getConnection(dataSource).prepareStatement(sql)) {
            setQueryParameters(preparedStatement, args);
            return extractResults(rowMapper, preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(String sql, Object... args) {
        try (final PreparedStatement preparedStatement =
                 DataSourceUtils.getConnection(dataSource).prepareStatement(sql)) {
            setQueryParameters(preparedStatement, args);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setQueryParameters(PreparedStatement preparedStatement, Object[] args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            PreparedStatementParameterSetter.setParameter(preparedStatement, i + 1, args[i]);
        }
    }

    private <T> List<T> extractResults(RowMapper<T> rowMapper, PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<T> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet));
            }
            return results;
        }
    }
}
