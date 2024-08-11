package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.jdbc.sql.SqlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ParameterMetaData;
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
        ParameterMetaData parameterMetaData = preparedStatement.getParameterMetaData();
        validateParameterCount(parameterMetaData, args);
        for (int i = 1; i <= args.length; i++) {
            Object parameter = args[i - 1];
            validateParameterType(parameterMetaData.getParameterType(i), parameter);
            preparedStatement.setObject(i, parameter);
        }
    }

    private static void validateParameterCount(ParameterMetaData parameterMetaData, Object... args) throws SQLException {
        if (parameterMetaData.getParameterCount() != args.length) {
            throw new IllegalArgumentException("쿼리 실행에 필요한 파리미터 수와 일치하지 않습니다.");
        }
    }

    private static void validateParameterType(int parameterType, Object parameter) {
        SqlType sqlType = SqlType.from(parameterType);
        if (!sqlType.isSqlType(parameter)) {
            throw new IllegalArgumentException("요청된 쿼리에 파라미터 타입과 불일치한 파라미터가 입력되었습니다.");
        }
    }
}
