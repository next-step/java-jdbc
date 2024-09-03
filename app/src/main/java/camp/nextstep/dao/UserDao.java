package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.DynamicResultSetHandler;
import camp.nextstep.jdbc.core.JdbcTemplate;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository
public class UserDao {

  private static final Logger log = LoggerFactory.getLogger(UserDao.class);
  private final JdbcTemplate jdbcTemplate;
  private final DynamicResultSetHandler<User> userHandler;


  @Autowired
  public UserDao(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.userHandler = new DynamicResultSetHandler<>(User.class);
  }

  public void insert(final User user) {
    final var sql = "INSERT INTO users (account, password, email) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail());
  }

  public void update(final User user) {
    final var sql = "UPDATE users SET account = ?, password = ?, email = ? WHERE id = ?";
    jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
  }

  public void update(final Connection connection, final User user) {
    final var sql = "UPDATE users SET account = ?, password = ?, email = ? WHERE id = ?";
    jdbcTemplate.update(connection, sql, user.getAccount(), user.getPassword(), user.getEmail(),
        user.getId());
  }

  public List<User> findAll() {
    String sql = "SELECT * FROM users";
    return jdbcTemplate.queryForList(sql, userHandler);
  }

  public User findById(final Long id) {
    final var sql = "SELECT id, account, password, email FROM users WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, userHandler, id);
  }

  public User findByAccount(final String account) {
    final var sql = "SELECT id, account, password, email FROM users WHERE account = ?";
    return jdbcTemplate.queryForObject(sql, userHandler, account);
  }
}