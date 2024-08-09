package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.dto.UserResultSetHandler;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.jdbc.core.Sql;
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
        Sql sql = new Sql("insert into users (account, password, email) values (?, ?, ?)", List.of(user.getAccount(), user.getPassword(), user.getEmail()));
        jdbcTemplate.update(sql);
    }

    public void update(final User user) {
        Sql sql = new Sql("update users set account = ?, password = ?, email = ? where id = ?", List.of(user.getAccount(), user.getPassword(), user.getEmail(), user.getId()));
        jdbcTemplate.update(sql);
    }

    public List<User> findAll() {
        Sql sql = new Sql("select id, account, password, email from users");
        return jdbcTemplate.selectAll(sql, userResultSetHandler);
    }

    public User findById(final Long id) {
        Sql sql = new Sql("select id, account, password, email from users where id = ?", id);
        return jdbcTemplate.selectOne(sql, userResultSetHandler)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findByAccount(final String account) {
        Sql sql = new Sql("select id, account, password, email from users where account = ?", account);
        return jdbcTemplate.selectOne(sql, userResultSetHandler)
                .orElseThrow(() -> new UserNotFoundException(account));
    }
}
