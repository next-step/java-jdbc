package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
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

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, final Object... parameters) {
        return doQuery(
                sql,
                (psmt) -> {
                    for (int i = 0; i < parameters.length; i++) {
                        psmt.setObject(i + 1, parameters[i]);
                    }
                    final ResultSet resultSet = psmt.executeQuery();
                    final List<T> result = new ArrayList<>();
                    while (resultSet.next()) {
                        result.add(rowMapper.mapRow(resultSet));
                    }
                    return result;
                }
        );
    }

    public <T> List<T> query(final String sql, final PreparedStatementSetter pss, final RowMapper<T> rowMapper) {
        return doQuery(
                sql,
                (psmt) -> {
                    pss.setValues(psmt);
                    final ResultSet resultSet = psmt.executeQuery();
                    final List<T> result = new ArrayList<>();
                    while (resultSet.next()) {
                        result.add(rowMapper.mapRow(resultSet));
                    }
                    return result;
                }
        );
    }

    public <T> T query(final String sql, final ResultSetExtractor<T> resultSetExtractor, final Object... parameters) {
        return doQuery(
                sql,
                (psmt) -> {
                    for (int i = 0; i < parameters.length; i++) {
                        psmt.setObject(i + 1, parameters[i]);
                    }
                    final ResultSet resultSet = psmt.executeQuery();
                    if (resultSet.next()) {
                        return resultSetExtractor.extractData(resultSet);
                    }
                    return null;
                }
        );
    }

    public <T> T query(final String sql, final PreparedStatementSetter pss,
                       final ResultSetExtractor<T> resultSetExtractor) {
        return doQuery(
                sql,
                (psmt) -> {
                    pss.setValues(psmt);
                    final ResultSet resultSet = psmt.executeQuery();
                    if (resultSet.next()) {
                        return resultSetExtractor.extractData(resultSet);
                    }
                    return null;
                }
        );
    }

    public void update(final String sql, final Object... parameters) {
        doExecute(
                sql,
                (psmt) -> {
                    for (int i = 0; i < parameters.length; i++) {
                        psmt.setObject(i + 1, parameters[i]);
                    }
                    psmt.executeUpdate();
                }
        );
    }

    public void update(final String sql, final PreparedStatementSetter pss) {
        doExecute(
                sql,
                (psmt) -> {
                    pss.setValues(psmt);
                    psmt.executeUpdate();
                }
        );
    }

    private <T> T doQuery(final String sql,
                          final SqlQueryFunction<PreparedStatement, T> sqlQueryFunction) {
        try (
                final Connection conn = dataSource.getConnection();
                final PreparedStatement psmt = conn.prepareStatement(sql)
        ) {
            return sqlQueryFunction.apply(psmt);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }

    private void doExecute(final String sql, final SqlExecuteFunction<PreparedStatement> sqlExecuteFunction) {
        try (
                final Connection conn = dataSource.getConnection();
                final PreparedStatement psmt = conn.prepareStatement(sql)
        ) {
            sqlExecuteFunction.run(psmt);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }
}
