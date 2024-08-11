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

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(final String sql, final Object... parameters) {
        log.debug("query : {}", sql);

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParams(parameters, pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(final PreparedStatementCreator psc, final KeyHolder keyHolder) {
        try (final PreparedStatement pstmt = psc.createPreparedStatement(dataSource.getConnection())) {
            pstmt.executeUpdate();

            setGeneratedKey(keyHolder, pstmt);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute update and retrieve keys", e);
        }
    }

    private void setGeneratedKey(final KeyHolder keyHolder, final PreparedStatement pstmt) throws SQLException {
        try (final ResultSet resultSet = pstmt.getGeneratedKeys()) {
            if (resultSet.next()) {
                final Object id = resultSet.getObject(1);
                validateGeneratedKey(id);

                keyHolder.addGeneratedKeys("id", (Number) id);
            }
        }
    }

    private void validateGeneratedKey(final Object id) throws SQLException {
        if (!(id instanceof Number)) {
            throw new SQLException("generated id is not number");
        }
    }

    public <T> T queryForObject(final String sql, final Object[] parameters, final RowMapper<T> rowMapper) {
        final List<T> results = query(sql, parameters, rowMapper);
        if (results.size() != 1) {
            throw new RuntimeException("Expected 1 result, got " + results.size());
        }
        return results.get(0);
    }

    public <T> List<T> query(final String sql, final Object[] parameters, final RowMapper<T> rowMapper) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParams(parameters, pstmt);

            return queryInternal(rowMapper, pstmt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> queryInternal(final RowMapper<T> rowMapper, final PreparedStatement pstmt) throws SQLException {
        try (final ResultSet resultSet = pstmt.executeQuery()) {
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        }
    }

    private void setParams(final Object[] parameters, final PreparedStatement pstmt) throws SQLException {
        for (int i = 1; i <= parameters.length; i++) {
            final Object parameter = parameters[i - 1];
            if (parameter instanceof String) {
                pstmt.setString(i, (String) parameter);
            } else if (parameter instanceof Integer) {
                pstmt.setInt(i, (Integer) parameter);
            } else if (parameter instanceof Long) {
                pstmt.setLong(i, (Long) parameter);
            }
        }
    }
}
