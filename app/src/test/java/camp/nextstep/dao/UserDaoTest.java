package camp.nextstep.dao;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.CleanUp.cleanUp;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest {

    private static final DataSource dataSource = new MyConfiguration().dataSource();
    private static final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    private static final UserDao userDao = new UserDao(jdbcTemplate);

    @BeforeAll
    static void setup() {
        DatabasePopulatorUtils.execute(dataSource);
        final var user = new User("gugu", "password", "hkkang@woowahan.com");
        userDao.insert(user);
    }

    @Test
    void findAll() {
        final var users = userDao.findAll();

        assertThat(users).isNotEmpty();
    }

    @Test
    @Order(1)
    void findById() {
        final var actual = userDao.findById(1L);

        assertThat(actual.getAccount()).isEqualTo("gugu");
    }

    @Test
    void findByAccount() {
        final var user = new User("find-account", "password", "hkkang@woowahan.com");
        userDao.insert(user);

        final var actual = userDao.findByAccount(user.getAccount());

        assertThat(actual.getAccount()).isEqualTo("find-account");
    }

    @Test
    void insert() {
        final var account = "insert-gugu";
        final var user = new User(account, "password", "hkkang@woowahan.com");
        userDao.insert(user);

        final var actual = userDao.findByAccount(user.getAccount());

        assertThat(actual.getAccount()).isEqualTo(account);
    }

    @Test
    @Order(2)
    void update() {
        final var user = new User(1L, "update-gugu", "password", "hkkang@woowahan.com");

        userDao.update(user);

        final var actual = userDao.findByAccount(user.getAccount());

        assertThat(actual.getAccount()).isEqualTo("update-gugu");
    }
}
