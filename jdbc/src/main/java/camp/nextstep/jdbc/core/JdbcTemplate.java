package camp.nextstep.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTemplate {
    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(String sql, List<?> params) {
        printLog(sql, params);
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setUpParameters(pstmt, params);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public <T> T selectOne(String sql, List<?> params, ResultSetHandler<T> resultSetHandler) {
        printLog(sql, params);

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setUpParameters(pstmt, params);

            return getOneResult(resultSetHandler, pstmt);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private <T> T getOneResult(ResultSetHandler<T> resultSetHandler, PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            rs.last();
            int rowNumber = rs.getRow();
            if (rowNumber == 1) {
                return resultSetHandler.handle(rs);
            }

            if (rowNumber < 1) {
                return null;
            }

            throw new NotSingleResultSetException();
        }
    }

    public <T> List<T> selectAll(String sql, ResultSetHandler<T> resultSetHandler) {
        return selectAll(sql, List.of(), resultSetHandler);
    }

    public <T> List<T> selectAll(String sql, List<?> params, ResultSetHandler<T> resultSetHandler) {
        printLog(sql, params);

        try(Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setUpParameters(pstmt, params);
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

    private void printLog(String sql, List<?> params) {
        log.info("query : {}, params : {}", sql, params);
    }

    private void setUpParameters(PreparedStatement pstmt, List<?> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            pstmt.setObject(i + 1, params.get(i));
        }
    }
}
