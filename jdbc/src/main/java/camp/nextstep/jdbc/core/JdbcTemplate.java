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
        checkParameterNum(sql, parameters);

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParams(parameters, pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcException("failed update - " + e.getMessage(), e);
        }
    }

    public <T> T queryForObject(final String sql, final Object[] parameters, final RowMapper<T> rowMapper) {
        final List<T> results = query(sql, parameters, rowMapper);
        if (results.size() != 1) {
            throw new JdbcException("Expected 1 result, got " + results.size());
        }
        return results.get(0);
    }

    public <T> List<T> query(final String sql, final Object[] parameters, final RowMapper<T> rowMapper) {
        checkParameterNum(sql, parameters);

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParams(parameters, pstmt);

            try (final ResultSet resultSet = pstmt.executeQuery()) {
                return mappingResult(rowMapper, resultSet);
            }
        } catch (Exception e) {
            throw new JdbcException("failed query - " + e.getMessage(), e);
        }
    }

    private void checkParameterNum(final String sql, final Object[] parameters) {
        final int requiredParameterNum = countRequiredParameter(sql);

        if (requiredParameterNum != parameters.length) {
            throw new JdbcException("Expected parameter num: " + requiredParameterNum + ", got " + parameters.length);
        }
    }

    private int countRequiredParameter(final String sql) {
        return sql.length() - sql.replace("?", "").length();
    }

    private <T> List<T> mappingResult(final RowMapper<T> rowMapper, final ResultSet resultSet) throws SQLException {
        final List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(rowMapper.mapRow(resultSet));
        }
        return result;
    }

    private void setParams(final Object[] parameters, final PreparedStatement pstmt) throws SQLException {
        final PreparedStatementSetter setter = new DefaultPreparedStatementSetter(parameters);
        setter.setValues(pstmt);
    }
}
