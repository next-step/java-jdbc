package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.IndexedQueryBuilder;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.jdbc.core.RowMapper;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {

    private static final RowMapper<User> USER_ROW_MAPPER = rs -> new User(
            rs.getLong("id"),
            rs.getString("account"),
            rs.getString("password"),
            rs.getString("email"));

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final User user) {
        final String sql = new IndexedQueryBuilder("users")
                .insert("account", "password", "email")
                .build();
        jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(final User user) {
        final String sql = new IndexedQueryBuilder("users")
                .update("password")
                .whereEq("id")
                .build();
        jdbcTemplate.update(sql, user.getPassword(), user.getId());
    }

    public List<User> findAll() {
        final String sql = new IndexedQueryBuilder("users")
                .select("id", "account", "password", "email")
                .build();
        return jdbcTemplate.query(sql, USER_ROW_MAPPER);
    }

    public User findById(final Long id) {
        final String sql = new IndexedQueryBuilder("users")
                .select("id", "account", "password", "email")
                .whereEq("id")
                .build();
        return jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, id);
    }

    public User findByAccount(final String account) {
        final String sql = new IndexedQueryBuilder("users")
                .select("id", "account", "password", "email")
                .whereEq("account")
                .build();
        return jdbcTemplate.queryForObject(
                sql,
                USER_ROW_MAPPER,
                account
        );
    }

}
