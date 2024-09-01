package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    final var sql = "INSERT INTO users (account, password, email) VALUES (?, ?, ?)";
    jdbcTemplate.update(sql, List.of(user.getAccount(), user.getPassword(), user.getEmail()));
  }

  public void update(final User user) {
    final var sql = "UPDATE users SET account = ?, password = ?, email = ? WHERE id = ?";
    jdbcTemplate.update(sql,
        List.of(user.getAccount(), user.getPassword(), user.getEmail(), user.getId()));
  }

  public List<User> findAll() {
    String sql = "SELECT * FROM users";
    return jdbcTemplate.queryForList(sql, this::mapRowToUser);
  }

  public User findById(final Long id) {
    final var sql = "SELECT id, account, password, email FROM users WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
  }

  public User findByAccount(final String account) {
    final var sql = "SELECT id, account, password, email FROM users WHERE account = ?";
    return jdbcTemplate.queryForObject(sql, this::mapRowToUser, account);
  }

  private User mapRowToUser(ResultSet rs) throws SQLException {
    return new User(
        rs.getLong("id"),
        rs.getString("account"),
        rs.getString("password"),
        rs.getString("email")
    );
  }
}