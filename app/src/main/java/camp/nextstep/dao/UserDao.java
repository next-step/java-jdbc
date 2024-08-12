package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private final DataSource dataSource;

    @Autowired
    public UserDao(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserDao(final JdbcTemplate jdbcTemplate) {
        this.dataSource = null;
    }

    public void insert(final User user) {
        final var sql = "insert into users (account, password, email) values (?, ?, ?)";
        executeUpdate(sql, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(final User user) {
        final var sql = "update users set password = ? where id = ?";
        executeUpdate(sql, user.getPassword(), user.getId());
    }

    private void executeUpdate(final String sql, final Object... params) {
        try (final Connection conn = dataSource.getConnection();
             final PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            log.info("query : {}", sql);

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public List<User> findAll() {
        final var sql = "select id, account, password, email from users";
        return query(sql,
                rs -> new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)));
    }

    public User findById(final Long id) {
        final var sql = "select id, account, password, email from users where id = ?";

        return queryForObject(
                sql,
                preparedStatement -> preparedStatement.setLong(1, id),
                rs -> new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4))
        );
    }

    public User findByAccount(final String account) {
        final var sql = "select id, account, password, email from users where account = ?";

        return queryForObject(
                sql,
                preparedStatement -> preparedStatement.setString(1, account),
                rs -> new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4))
        );
    }

    private <T> List<T> query(final String sql, final PreparedStatementSetter preparedStatementSetter, final RowMapper<T> rowMapper) {
        try (final Connection conn = dataSource.getConnection();
             final PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (preparedStatementSetter != null) {
                preparedStatementSetter.setValues(pstmt);
            }

            final ResultSet rs = pstmt.executeQuery();

            log.info("query : {}", sql);

            final List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rowMapper.mapRow(rs));
            }
            return result;
        } catch (final SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
        return query(sql, null, rowMapper);
    }

    private <T> T queryForObject(final String sql, final PreparedStatementSetter preparedStatementSetter, final RowMapper<T> rowMapper) {
        final List<T> result = query(sql, preparedStatementSetter, rowMapper);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }


    @FunctionalInterface
    private interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    private interface PreparedStatementSetter {
        void setValues(PreparedStatement preparedStatement) throws SQLException;
    }
}
