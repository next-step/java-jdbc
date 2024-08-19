package camp.nextstep.jdbc.core;

import camp.nextstep.exception.JdbcException;
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

    public void update(final String sql, Object... args) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParams(args, pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper, final Object... args) {
        final List<T> results = executeQuery(sql, rowMapper, args);
        if (results.size() != 1) {
            throw new JdbcException("Expected 1 result, got " + results.size());
        }
        return results.get(0);
    }

    private <T> List<T> executeQuery(final String sql, final RowMapper rowMapper, final Object... args) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParams(args, pstmt);

            try (final ResultSet resultSet = pstmt.executeQuery()) {
                return mappingResult(rowMapper, resultSet);
            }
        } catch (Exception e) {
            throw new JdbcException("failed query - " + e.getMessage(), e);
        }
    }

    private <T> List<T> mappingResult(final RowMapper<T> rowMapper, final ResultSet resultSet) throws SQLException {
        final List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(rowMapper.mapRow(resultSet));
        }
        return result;
    }

    private void setParams(final Object[] args, final PreparedStatement pstmt) throws SQLException {
        for (int i = 1; i <= args.length; i++) {
            pstmt.setObject(i, args[i - 1]);
        }
    }
}
