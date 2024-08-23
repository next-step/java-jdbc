package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.jdbc.core.RowMapper;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;
import org.h2.result.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final User user) {
        final var sql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.execute(sql, user.getAccount(), user.getPassword(), user.getEmail());

    }

    public void update(final User user) {
        final var sql = "UPDATE users SET account = ?, password = ?, email = ? WHERE id = ?";
        jdbcTemplate.execute(sql, user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
    }

    public List<User> findAll() {
        final var sql = "SELECT id, account, password, email FROM users";

        return jdbcTemplate.query(sql, UserRowMappers.GET);
    }

    public User findById(final Long id) {
        final var sql = "SELECT id, account, password, email FROM users WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, UserRowMappers.GET, id);
    }

    public User findByAccount(final String account) {
        final var sql = "SELECT id, account, password, email FROM users WHERE account = ?";

        return jdbcTemplate.queryForObject(sql, UserRowMappers.GET, account);
    }

    private static final class UserRowMappers {

        public static final RowMapper<User> GET = user -> new User(user.getLong("id"),
            user.getString("account"),
            user.getString("password"),
            user.getString("email"));

    }
}
