package camp.nextstep.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class JdbcTemplate {

  private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

  private final DataSource dataSource;

  public JdbcTemplate(final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public int update(String sql, List<?> params) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      setParameters(preparedStatement, params);
      return preparedStatement.executeUpdate();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  public void insert() {
    // todo
  }

  public <T> T selectAll(String sql) {
    return null;
  }

  public <T> List<T> selectAll(String sql, List<?> params, ResultSetHandler<T> resultSetHandler) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      setParameters(preparedStatement, params);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        List<T> results = new ArrayList<>();
        while (resultSet.next()) {
          results.add(resultSetHandler.handle(resultSet));
        }
        return results;
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  private void setParameters(PreparedStatement preparedStatement, List<?> params) throws SQLException {
    for (int i = 0; i < params.size(); i++) {
      preparedStatement.setObject(i + 1, params.get(i));
    }
  }
}

