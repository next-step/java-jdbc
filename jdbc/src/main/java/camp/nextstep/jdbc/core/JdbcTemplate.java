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

    public <T> List<T> query(final String sql, RowMapper<T> rowMapper, Object... parameters) {
        return doQuery(
                sql, parameters,
                (psmt) -> {
                    ResultSet resultSet = psmt.executeQuery();
                    final var result = new ArrayList<T>();
                    while (resultSet.next()) {
                        result.add(rowMapper.mapRow(resultSet));
                    }
                    return result;
                }
        );
    }

    public <T> T query(final String sql, ResultSetExtractor<T> resultSetExtractor, Object... parameters) {
        return doQuery(
                sql, parameters,
                (psmt) -> {
                    ResultSet resultSet = psmt.executeQuery();
                    if (resultSet.next()) {
                        return resultSetExtractor.extractData(resultSet);
                    }
                    return null;
                }
        );
    }

    public void update(String sql, Object... parameters) {
        doExecute(
                sql, parameters,
                (psmt) -> psmt.executeUpdate()
        );
    }

    private <T> T doQuery(final String sql,
                          Object[] parameters,
                          SqlQueryFunction<PreparedStatement, T> block) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement psmt = conn.prepareStatement(sql)
        ) {
            for (int i = 0; i < parameters.length; i++) {
                psmt.setObject(i + 1, parameters[i]);
            }

            return block.apply(psmt);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }

    private void doExecute(final String sql,
                           Object[] parameters,
                           SqlExecuteFunction<PreparedStatement> block) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement psmt = conn.prepareStatement(sql)
        ) {
            for (int i = 0; i < parameters.length; i++) {
                psmt.setObject(i + 1, parameters[i]);
            }

            block.run(psmt);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }
}
