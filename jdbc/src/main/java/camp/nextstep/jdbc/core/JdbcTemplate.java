package camp.nextstep.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(String sql, List<Object> params) {
        Connection conn = getConnection();
        runUpdateQuery(conn, sql, params);
    }

    public void update(String sql, List<Object> params) {
        Connection conn = getConnection();
        runUpdateQuery(conn, sql, params);
    }

    public void delete(String sql, List<Object> params) {
        Connection conn = getConnection();
        runUpdateQuery(conn, sql, params);
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rm) {
        return queryForObject(sql, rm, new ArrayList<>());
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rm, final List<Object> params) {
        final var list = query(sql, rm, params);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rm) {
        return query(sql, rm, new ArrayList<>());
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rm, final List<Object> params) {
        PreparedStatement pstmt = null;
        Connection conn = getConnection();
        List<T> result = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);

            log.debug("query : {}", sql);
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i).toString());
            }
            final var resultSet = pstmt.executeQuery();
            while (true) {
                try {
                    if (!resultSet.next()) break;
                    result.add(rm.mapRow(resultSet));
                } catch (SQLException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ignored) {
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
            return result;
        }
    }

    private void runUpdateQuery(Connection conn, String sql, List<Object> params) {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

            log.debug("query : {}", sql);
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i).toString());
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ignored) {}

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
