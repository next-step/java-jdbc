package camp.nextstep.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(final String sql, final Object... parameters) {
        log.debug("query : {}", sql);

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParams(parameters, pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setParams(final Object[] parameters, final PreparedStatement pstmt) throws SQLException {
        for (int i = 1; i <= parameters.length; i++) {
            final Object parameter = parameters[i - 1];
            if (parameter instanceof String) {
                pstmt.setString(i, (String) parameter);
            } else if (parameter instanceof Integer) {
                pstmt.setInt(i, (Integer) parameter);
            }
        }
    }
}
