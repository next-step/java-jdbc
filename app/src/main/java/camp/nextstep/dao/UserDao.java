package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.jdbc.core.RowMapperImpl;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository
public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private static final String INSERT_SQL = "insert into users (account, password, email) values (?, ?, ?)";
    private static final String UPDATE_SQL = "update users set account = ?, password = ?, email = ? where id = ?";
    private static final String SELECT_ALL_SQL = "select id, account, password, email from users";
    private static final String SELECT_BY_ID_SQL = "select id, account, password, email from users where id = ?";
    private static final String SELECT_BY_ACCOUNT_SQL = "select id, account, password, email from users where account = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final User user) {
        jdbcTemplate.update(INSERT_SQL, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(final User user) {
        jdbcTemplate.update(UPDATE_SQL, user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
    }

    public List<User> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, new RowMapperImpl<>(User.class));
    }

    public User findById(final Long id) {
        return jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new RowMapperImpl<>(User.class), id);
    }

    public User findByAccount(final String account) {
        return jdbcTemplate.queryForObject(SELECT_BY_ACCOUNT_SQL, new RowMapperImpl<>(User.class), account);
    }
}
