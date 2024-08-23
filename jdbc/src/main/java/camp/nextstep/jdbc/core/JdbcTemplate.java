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

    public <T> List<T> query(final String sql, final PreparedStatementSetter pss, final RowMapper<T> rowMapper) {
        return doQuery(sql, psmt -> {
            pss.setValues(psmt);
            final ResultSet resultSet = psmt.executeQuery();
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        });
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, final Object... parameters) {
        return this.query(sql, new PreparedStatementSetterImpl(parameters), rowMapper);
    }

    public <T> T query(final String sql, final PreparedStatementSetter pss,
                       final ResultSetExtractor<T> resultSetExtractor) {
        return doQuery(sql, psmt -> {
            pss.setValues(psmt);
            final ResultSet resultSet = psmt.executeQuery();
            if (resultSet.next()) {
                return resultSetExtractor.extractData(resultSet);
            }
            return null;
        });
    }

    public <T> T query(final String sql, final ResultSetExtractor<T> resultSetExtractor, final Object... parameters) {
        return query(sql, new PreparedStatementSetterImpl(parameters), resultSetExtractor);
    }

    private <T> T doQuery(final String sql, final SqlQueryFunction<PreparedStatement, T> sqlQueryFunction) {
        try (final Connection conn = dataSource.getConnection();
             final PreparedStatement psmt = conn.prepareStatement(sql)) {
            return sqlQueryFunction.apply(psmt);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }

    public void update(final String sql, final Object... parameters) {
        update(sql, new PreparedStatementSetterImpl(parameters));
    }

    public void update(final String sql, final PreparedStatementSetter pss) {
        try (final Connection connection = dataSource.getConnection()) {
            update(connection, sql, pss);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }

    public void update(final Connection connection, final String sql, final Object... parameters) {
        update(connection, sql, new PreparedStatementSetterImpl(parameters));
    }

    public void update(final Connection connection, final String sql, final PreparedStatementSetter pss) {
        doExecute(connection, sql, psmt -> {
            pss.setValues(psmt);
            psmt.executeUpdate();
        });
    }

    private static void doExecute(final Connection connection,
                                  final String sql,
                                  final SqlExecuteFunction<PreparedStatement> sqlExecuteFunction) {
        try (final PreparedStatement psmt = connection.prepareStatement(sql)) {
            sqlExecuteFunction.run(psmt);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }
}
