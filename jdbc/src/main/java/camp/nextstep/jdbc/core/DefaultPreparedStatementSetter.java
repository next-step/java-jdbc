package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DefaultPreparedStatementSetter implements PreparedStatementSetter  {

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
            throw new DataAccessException(e);
        }
    }

    private void prepareArguments(PreparedStatement preparedStatement, Object... args) throws SQLException {
        for (int i = 1; i <= args.length; i++) {
            preparedStatement.setObject(i, args[i - 1]);
        }
    }
}
