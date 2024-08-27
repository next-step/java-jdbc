package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import com.interface21.context.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

@Repository
public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final User user) {
        final var sql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(final User user) {
        final var sql = "update users set account=?, password=?, email=? where id=?";
        jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
    }

    public void update(final Connection connection, final User user) {
        final var sql = "update users set account=?, password=?, email=? where id=?";
        jdbcTemplate.update(connection, sql, user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
    }

    public List<User> findAll() {
        final var sql = "select * from users";
        return jdbcTemplate.executeQuery(sql, UserRowMapper.getInstance());
    }

    public User findById(final Long id) {
        final var sql = "select id, account, password, email from users where id = ?";
        return jdbcTemplate.execurteQueryForObject(sql, UserRowMapper.getInstance(), id);
    }

    public User findByAccount(final String account) {
        final var sql = "select id, account, password, email from users where account = ?";
        return jdbcTemplate.execurteQueryForObject(sql, UserRowMapper.getInstance(), account);
    }
}
