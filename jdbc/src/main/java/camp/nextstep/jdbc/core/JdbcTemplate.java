package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class JdbcTemplate {
    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(String query, Object... args) {
        validateCountOfParameter(query, args);
        update(query, new ArgumentPreparedStatementSetter(args));
    }

    public void update(String query, PreparedStatementSetter preparedStatementSetter) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                preparedStatementSetter.setValues(pstmt);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            closeConnection(connection);
        }
    }

    private void releaseConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DataAccessException("JDBC Connection Release 실패");
        }
    }

    private Connection getConnection() throws SQLException {
        if (TransactionSynchronizationManager.isTransactionActive(dataSource)) {
            return TransactionSynchronizationManager.getResource(dataSource);
        }

        return dataSource.getConnection();
    }

    public <T> Optional<T> selectOne(String query, ResultSetHandler<T> resultSetHandler, Object... args) {
        validateCountOfParameter(query, args);
        return selectOne(query, new ArgumentPreparedStatementSetter(args), resultSetHandler);
    }

    public <T> Optional<T> selectOne(String query, PreparedStatementSetter preparedStatementSetter, ResultSetHandler<T> resultSetHandler) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                preparedStatementSetter.setValues(pstmt);
                return findOneResult(resultSetHandler, pstmt);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null && !TransactionSynchronizationManager.isTransactionActive(dataSource)) {
            releaseConnection(connection);
        }
    }

    private <T> Optional<T> findOneResult(ResultSetHandler<T> resultSetHandler, PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            rs.last();
            int rowNumber = rs.getRow();
            if (rowNumber == 1) {
                return Optional.of(resultSetHandler.handle(rs));
            }

            if (rowNumber == 0) {
                return Optional.empty();
            }

            throw new NotSingleResultSetException();
        }
    }

    public <T> List<T> selectAll(String query, ResultSetHandler<T> resultSetHandler) {
        return selectAll(query, pstmt -> {}, resultSetHandler);
    }

    public <T> List<T> selectAll(String query, ResultSetHandler<T> resultSetHandler, Object... args) {
        validateCountOfParameter(query, args);
        return selectAll(query, new ArgumentPreparedStatementSetter(args), resultSetHandler);
    }

    public <T> List<T> selectAll(String query, PreparedStatementSetter preparedStatementSetter, ResultSetHandler<T> resultSetHandler) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                preparedStatementSetter.setValues(pstmt);
                return getMultipleResults(resultSetHandler, pstmt);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            closeConnection(connection);
        }
    }

    private <T> List<T> getMultipleResults(ResultSetHandler<T> resultSetHandler, PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(resultSetHandler.handle(rs));
            }
            return results;
        }
    }

    private void validateCountOfParameter(String query, Object... args) {
        long queryParameterCounts = countParameters(query);
        int argumentCounts = args.length;
        if (queryParameterCounts != argumentCounts) {
            throw new IllegalArgumentException("query에 명시된 매개변수 개수와 전달받은 인자 개수가 불일치합니다. queryParameterCounts=%s, argumentCounts=%s".formatted(queryParameterCounts, argumentCounts));
        }
    }

    private long countParameters(String query) {
        return query.chars()
                .filter(c -> c == '?')
                .count();
    }
}
