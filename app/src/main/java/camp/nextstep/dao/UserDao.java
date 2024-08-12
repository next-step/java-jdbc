package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.dto.UserResultSetHandler;
import camp.nextstep.jdbc.core.JdbcTemplate;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final UserResultSetHandler userResultSetHandler;

    @Autowired
    public UserDao(final JdbcTemplate jdbcTemplate, UserResultSetHandler userResultSetHandler) {
        this.jdbcTemplate = jdbcTemplate;
        this.userResultSetHandler = userResultSetHandler;
    }

    public void insert(final User user) {
        String query = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.update(query, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(final User user) {
        String query = "update users set account = ?, password = ?, email = ? where id = ?";
        jdbcTemplate.update(query, user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
    }

    public List<User> findAll() {
        String query = "select id, account, password, email from users";
        return jdbcTemplate.selectAll(query, userResultSetHandler);
    }

    public User findById(final Long id) {
        String query = "select id, account, password, email from users where id = ?";
        return jdbcTemplate.selectOne(query, userResultSetHandler, id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findByAccount(final String account) {
        String query = "select id, account, password, email from users where account = ?";
        return jdbcTemplate.selectOne(query, userResultSetHandler, account)
                .orElseThrow(() -> new UserNotFoundException(account));
    }
}
