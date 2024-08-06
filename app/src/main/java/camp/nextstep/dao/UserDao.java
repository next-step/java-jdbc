package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public UserDao(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public UserDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final User user) {
        final var sql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.insert(sql, List.of(user.getAccount(), user.getPassword(), user.getEmail()));
    }

    public void update(final User user) {
        // todo
    }

    public List<User> findAll() {
        // todo
        return null;
    }

    public User findById(final Long id) {
        final var sql = "select id, account, password, email from users where id = ?";
        return jdbcTemplate.selectOne(sql, List.of(id), (rs) -> {
            try {
                return new User(
                        rs.getInt("id"),
                        rs.getString("account"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public User findByAccount(final String account) {
        final var sql = "select id, account, password, email from users where account = ?";
        return jdbcTemplate.selectOne(sql, List.of(account), (rs) -> {
            try {
                return new User(
                        rs.getInt("id"),
                        rs.getString("account"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
