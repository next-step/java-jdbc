package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DefaultPreparedStatementSetter implements PreparedStatementSetter {

    private static final Logger log = LoggerFactory.getLogger(DefaultPreparedStatementSetter.class);

    private final DataSource dataSource;

    public DefaultPreparedStatementSetter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> T executeQuery(String sql, PreparedStatementParser<T> preparedStatementParser, Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            prepareArguments(preparedStatement, args);

            return preparedStatementParser.parse(preparedStatement);
        } catch (SQLException e) {
            log.warn("쿼리 실행에 오류가 발생했습니다. sql : {}", sql);
            throw new DataAccessException("쿼리 실행 시 오류 발생", e);
        }
    }

    private void prepareArguments(PreparedStatement preparedStatement, Object... args) throws SQLException {
        for (int i = 1; i <= args.length; i++) {
            preparedStatement.setObject(i, args[i - 1]);
        }
    }
}
