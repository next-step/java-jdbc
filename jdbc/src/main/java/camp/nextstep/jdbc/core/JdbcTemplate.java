package camp.nextstep.jdbc.core;

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

    public void update(Sql sql, PreparedStatementSetter preparedStatementSetter) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.getQuery())) {
            preparedStatementSetter.setValues(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public <T> Optional<T> selectOne(Sql sql, PreparedStatementSetter preparedStatementSetter, ResultSetHandler<T> resultSetHandler) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.getQuery())) {
            preparedStatementSetter.setValues(pstmt);
            return getOneResult(resultSetHandler, pstmt);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private <T> Optional<T> getOneResult(ResultSetHandler<T> resultSetHandler, PreparedStatement pstmt) throws SQLException {
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

    public <T> List<T> selectAll(Sql sql, ResultSetHandler<T> resultSetHandler) {
        return selectAll(sql, pstmt -> {}, resultSetHandler);
    }

    public <T> List<T> selectAll(Sql sql, PreparedStatementSetter preparedStatementSetter, ResultSetHandler<T> resultSetHandler) {
        try(Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.getQuery())) {
            preparedStatementSetter.setValues(pstmt);
            return getMultipleResults(resultSetHandler, pstmt);
        } catch (SQLException e) {
            throw new DatabaseException(e);
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
}
