package camp.nextstep.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> query(final String sql, RowMapper<T> rowMapper, Object... parameters) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement psmt = conn.prepareStatement(sql)
        ) {
            for (int i = 0; i < parameters.length; i++) {
                psmt.setObject(i + 1, parameters[i]);
            }

            ResultSet resultSet = psmt.executeQuery();

            final var result = new ArrayList<T>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public <T> T query(final String sql, ResultSetExtractor<T> resultSetExtractor, Object... parameters) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement psmt = conn.prepareStatement(sql)
        ) {
            for (int i = 0; i < parameters.length; i++) {
                psmt.setObject(i + 1, parameters[i]);
            }

            ResultSet resultSet = psmt.executeQuery();
            if (resultSet.next()) {
                return resultSetExtractor.extractData(resultSet);
            }
            return null;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void update(String sql, Object... parameters) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement psmt = conn.prepareStatement(sql)
        ) {
            for (int i = 0; i < parameters.length; i++) {
                psmt.setObject(i + 1, parameters[i]);
            }
            psmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
