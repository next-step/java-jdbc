package camp.nextstep.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class JdbcTemplate {

  private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

  private final DataSource dataSource;

  public JdbcTemplate(final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public <T> List<T> selectAll(String sql, List<?> params, ResultSetHandler<T> resultSetHandler) {

    try (Connection conn = dataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      setUpParameters(pstmt, params);
      return getMultipleResults(resultSetHandler, pstmt);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  private <T> List<T> getMultipleResults(ResultSetHandler<T> resultSetHandler,
      PreparedStatement pstmt) throws SQLException {
    try (ResultSet rs = pstmt.executeQuery()) {
      List<T> results = new ArrayList<>();
      while (rs.next()) {
        results.add(resultSetHandler.handle(rs));
      }
      return results;
    }
  }

  private void setUpParameters(PreparedStatement pstmt, List<?> params) throws SQLException {
    for (int i = 0; i < params.size(); i++) {
      pstmt.setObject(i + 1, params.get(i));
    }
  }
}

