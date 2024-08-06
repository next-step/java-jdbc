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

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            setUpParameters(pstmt, params);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            postProcessing(pstmt);
            postProcessing(conn);
        }
    }

    public <T> T selectOne(String sql, List<?> params, ResultSetHandler<T> resultSetHandler) {
        printLog(sql, params);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            setUpParameters(pstmt, params);

            rs = pstmt.executeQuery();
            if (!rs.next()) {
                return null;
            }

            return resultSetHandler.handle(rs);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            postProcessing(rs);
            postProcessing(pstmt);
            postProcessing(conn);
        }
    }

    public <T> List<T> select(String sql, ResultSetHandler<T> resultSetHandler) {
        return select(sql, List.of(), resultSetHandler);
    }

    public <T> List<T> select(String sql, List<?> params, ResultSetHandler<T> resultSetHandler) {
        printLog(sql, params);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            setUpParameters(pstmt, params);

            rs = pstmt.executeQuery();
            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(resultSetHandler.handle(rs));
            }
            return results;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            postProcessing(rs);
            postProcessing(pstmt);
            postProcessing(conn);
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

    private void postProcessing(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ignored) {}
    }

    private void postProcessing(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ignored) {}
    }

    private void postProcessing(PreparedStatement pstmt) {
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
