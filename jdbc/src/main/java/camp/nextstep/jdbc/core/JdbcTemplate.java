package camp.nextstep.jdbc.core;

import camp.nextstep.transaction.support.TransactionSynchronizationManager;
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
    private static final Object[] EMPTY = new Object[0];

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(final String sql, final Object... params) {
        update(sql, new IndexedPreparedStatementSetter(params));
    }

    public void update(final String sql, final PreparedStatementSetter preparedStatementSetter) {
        final Connection conn = getConnection();
        try (final PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            log.info("query : {}", sql);

            preparedStatementSetter.setValues(preparedStatement);

            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        } finally {
            tryToCloseConnection(conn);
        }
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, final Object... params) {
        return query(sql, rowMapper, new IndexedPreparedStatementSetter(params));
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, final PreparedStatementSetter preparedStatementSetter) {
        ResultSet resultSet = null;
        final Connection conn = getConnection();
        try (final PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatementSetter.setValues(preparedStatement);

            resultSet = preparedStatement.executeQuery();

            log.info("query : {}", sql);

            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        } finally {
            tryToCloseConnection(conn);
            closeResultSet(resultSet);
        }
    }

    private void closeResultSet(final ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (final SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
        return query(sql, rowMapper, EMPTY);
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper, final Object... params) {
        final List<T> result = query(sql, rowMapper, params);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    private Connection getConnection() {
        final Connection connection = TransactionSynchronizationManager.getResource(dataSource);

        if (connection != null) {
            return connection;
        }

        try {
            return dataSource.getConnection();
        } catch (final SQLException e) {
            throw new DataAccessException(e);
        }

    }

    private void tryToCloseConnection(final Connection connection) {
        final Connection currnetContextConnection = TransactionSynchronizationManager.getResource(dataSource);
        if (currnetContextConnection != null) {
            return;
        }

        closeConnection(connection);
    }

    private void closeConnection(final Connection connection) {
        try {
            connection.close();
        } catch (final SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
