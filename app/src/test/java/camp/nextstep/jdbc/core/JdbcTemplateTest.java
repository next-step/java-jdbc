package camp.nextstep.jdbc.core;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.dao.UserRowMapper;
import camp.nextstep.domain.User;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static support.CleanUp.cleanUp;

class JdbcTemplateTest {

    private static final DataSource dataSource = new MyConfiguration().dataSource();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    @BeforeAll
    static void setUp() {
        DatabasePopulatorUtils.execute(dataSource);
    }

    @AfterEach
    void tearDown() {
        cleanUp(dataSource);
    }

    @DisplayName("insert query 를 받아 실행 하면 데이터가 저장 된다")
    @Test
    public void update_insert() throws Exception {
        // given
        final User user = new User(1L, "account1", "password", "email");
        final var sql = "insert into users (account, password, email) values (?, ?, ?)";

        // when
        jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail());

        // then
        final User actual = findUserByAccount(user.getAccount());
        assertThat(actual).isNotNull()
                .extracting("account", "password", "email")
                .contains("account1", "password", "email");
    }

    @DisplayName("update query 를 받아 실행 하면 데이터가 수정 된다")
    @Test
    public void update_update() throws Exception {
        // given
        final User user = new User(1L, "account1", "password", "email");
        final var insertSql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.update(insertSql, user.getAccount(), user.getPassword(), user.getEmail());

        final User updateUser = new User(1L, "account2", "password1", "email1");
        final var updateSql = "update users set account=?, password=?, email=? where account=?";

        // when
        jdbcTemplate.update(updateSql, updateUser.getAccount(), updateUser.getPassword(), updateUser.getEmail(), user.getAccount());

        // then
        final User actual = findUserByAccount(updateUser.getAccount());
        assertThat(actual).isNotNull()
                .extracting("account", "password", "email")
                .contains("account2", "password1", "email1");
    }

    @DisplayName("delete query 를 받아 실행 하면 데이터가 삭제 된다")
    @Test
    public void update_delete() throws Exception {
        // given
        final User user = new User(1L, "account3", "password", "email");
        final var insertSql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.update(insertSql, user.getAccount(), user.getPassword(), user.getEmail());

        final var deleteSql = "delete from users where account=?";

        // when
        jdbcTemplate.update(deleteSql, user.getAccount());

        // then
        final User actual = findUserByAccount(user.getAccount());
        assertThat(actual).isNull();
    }

    @DisplayName("select query 와 파라미터를 이용해 단일 객체를 조회 한다")
    @Test
    public void queryForObject() throws Exception {
        // given
        final User user = new User(1L, "account4", "password", "email");
        final var insertSql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.update(insertSql, user.getAccount(), user.getPassword(), user.getEmail());

        final var sql = "select * from users where account=?";

        // when
        final User actual = jdbcTemplate.queryForObject(sql, new Object[]{user.getAccount()}, new UserRowMapper());

        // then
        assertThat(actual).isNotNull()
                .extracting("account", "password", "email")
                .contains("account4", "password", "email");
    }

    @DisplayName("select 쿼리로 단일 객체를 조회할 때, 조회 결과가 두개 이상인 경우 예외를 던진다")
    @Test
    public void queryForObject_duplicated() throws Exception {
        // given
        final User user = new User(1L, "account5", "password", "email");
        final var insertSql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.update(insertSql, user.getAccount(), user.getPassword(), user.getEmail());
        jdbcTemplate.update(insertSql, user.getAccount(), user.getPassword(), user.getEmail());

        final var sql = "select * from users where account=?";

        // when then
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(sql, new Object[]{user.getAccount()}, new UserRowMapper()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 result, got 2");
    }

    @DisplayName("select 쿼리로 단일 객체를 조회할 때, 조회 결과가 없는 경우 예외를 던진다")
    @Test
    public void queryForObject_empty() throws Exception {
        // given
        final User user = new User(1L, "account6", "password", "email");
        final var sql = "select * from users where account=?";

        // when then
        assertThatThrownBy(() -> jdbcTemplate.queryForObject(sql, new Object[]{user.getAccount()}, new UserRowMapper()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 result, got 0");
    }

    @DisplayName("select 쿼리로 리스트 조회를 한다")
    @Test
    public void query() throws Exception {
        // given
        for (long i = 0; i < 10; i++) {
            final User user = new User(i, "account7 - " + i, "password", "email");
            final var sql = "insert into users (account, password, email) values (?, ?, ?)";
            jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail());
        }
        final var sql = "select * from users";

        // when
        final List<User> actual = jdbcTemplate.query(sql, new Object[]{}, new UserRowMapper());

        // then
        assertThat(actual).hasSize(10)
                .extracting(User::getAccount)
                .containsExactlyInAnyOrder(
                        "account7 - 0", "account7 - 1", "account7 - 2", "account7 - 3", "account7 - 4", "account7 - 5", "account7 - 6", "account7 - 7", "account7 - 8", "account7 - 9"
                );
    }

    private User findUserByAccount(final String account) {
        final String sql = "SELECT id, account, password, email FROM users WHERE account = ?";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, account);

            try (final ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getLong("id"),
                            resultSet.getString("account"),
                            resultSet.getString("password"),
                            resultSet.getString("email")
                    );
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
