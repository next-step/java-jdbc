package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        jdbcTemplate.update(sql, List.of(user.getAccount(), user.getPassword(), user.getEmail()));
    }

    public void update(final User user) {
        String sql = "update users set account = ?, password = ?, email = ? where id = ?";
        jdbcTemplate.update(sql, List.of(user.getAccount(), user.getPassword(), user.getEmail(), user.getId()));
    }

    public List<User> findAll() {
        String sql = "select id, account, password, email from users";
        return jdbcTemplate.select(sql, (rs) -> {
            try {
                return createUser(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public User findById(final Long id) {
        final var sql = "select id, account, password, email from users where id = ?";
        return jdbcTemplate.selectOne(sql, List.of(id), (rs) -> {
            try {
                return createUser(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public User findByAccount(final String account) {
        final var sql = "select id, account, password, email from users where account = ?";
        return jdbcTemplate.selectOne(sql, List.of(account), (rs) -> {
            try {
                return createUser(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private User createUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("account"),
                rs.getString("password"),
                rs.getString("email")
        );
    }
}
