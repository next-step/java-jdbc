package camp.nextstep.dao;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.CleanUp.cleanUp;

class UserDaoTest {

    private static final DataSource dataSource = new MyConfiguration().dataSource();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    private final UserDao userDao = new UserDao(jdbcTemplate);

    @BeforeAll
    static void setup() {
        DatabasePopulatorUtils.execute(dataSource);
    }

    @AfterEach
    void tearDown() {
        cleanUp(dataSource);
    }

    @Test
    void findAll() {
        final var user = new User("account", "password", "hkkang@woowahan.com");
        userDao.insert(user);

        final var users = userDao.findAll();

        assertThat(users).hasSize(1);
    }

    @Test
    void findById() {
        final var user = new User("account", "password", "hkkang@woowahan.com");
        userDao.insert(user);

        final var actual = userDao.findById(user.getId());

        assertThat(actual.getAccount()).isEqualTo("account");
    }

    @Test
    void findByAccount() {
        final var user = new User("account", "password", "hkkang@woowahan.com");
        userDao.insert(user);

        final var actual = userDao.findByAccount(user.getAccount());

        assertThat(actual.getAccount()).isEqualTo("account");
    }

    @Test
    void insert() {
        final var account = "insert-gugu";
        final var user = new User(account, "password", "hkkang@woowahan.com");
        userDao.insert(user);

        final var actual = userDao.findById(user.getId());

        assertThat(actual.getAccount()).isEqualTo(account);
    }

    @Test
    void update() {
        final var user = new User("account", "password", "hkkang@woowahan.com");
        userDao.insert(user);
        final var newPassword = "password99";
        user.changePassword(newPassword);

        userDao.update(user);

        final var actual = userDao.findById(user.getId());

        assertThat(actual.getPassword()).isEqualTo(newPassword);
    }
}
