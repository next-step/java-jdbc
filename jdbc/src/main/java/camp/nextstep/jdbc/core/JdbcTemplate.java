package camp.nextstep.jdbc.core;

import camp.nextstep.dao.QueryFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static camp.nextstep.util.StringUtils.countContainSequence;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);
    private static final String QUERY_PLACEHOLDER = "?";

    private final PreparedStatementSetter preparedStatementSetter;
    private final ResultSetSetter resultSetSetter;

    public JdbcTemplate(final DataSource dataSource) {
        this.preparedStatementSetter = new DefaultPreparedStatementSetter(dataSource);
        this.resultSetSetter = new DefaultResultSetSetter();
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        validateSql(sql, args);
        return preparedStatementSetter.executeQuery(
                sql,
                preparedStatement -> resultSetSetter.parse(rowMapper, preparedStatement),
                args
        );
    }

    public <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        validateSql(sql, args);
        return preparedStatementSetter.executeQuery(
                sql,
                preparedStatement -> resultSetSetter.parseToObject(rowMapper, preparedStatement),
                args
        );
    }

    public int update(String sql, Object... args) {
        validateSql(sql, args);
        return preparedStatementSetter.executeQuery(sql, PreparedStatement::executeUpdate, args);
    }

    public int update(Connection connection, String sql, Object... args) {
        validateSql(sql, args);
        return preparedStatementSetter.executeQuery(connection, sql, PreparedStatement::executeUpdate, args);
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
}
