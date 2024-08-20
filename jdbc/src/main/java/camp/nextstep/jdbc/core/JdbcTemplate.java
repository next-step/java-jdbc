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

    public void update(String sql, Object... params) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            log.debug("query : {}", sql);

            setParameters(params, preparedStatement);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... params) {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            log.debug("query : {}", sql);

            setParameters(params, preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... params) {
        return query(sql, rowMapper, params).stream()
                                            .findFirst()
                                            .orElse(null);
    }

    private void setParameters(Object[] params, PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
    }
}
