package camp.nextstep.dao;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    void setup() {
        final var myConfiguration = new MyConfiguration();
        final var dataSource = myConfiguration.dataSource();
        DatabasePopulatorUtils.execute(dataSource);

        final var jdbcTemplate = myConfiguration.jdbcTemplate(dataSource);
        userDao = new UserDao(jdbcTemplate);
        final var user = new User("gugu", "password", "hkkang@woowahan.com");
        userDao.insert(user);
    }

    @Test
    void findAll() {
        final var users = userDao.findAll();

        assertThat(users).isNotEmpty();
    }

    @Test
    void findById() {
        final var user = userDao.findById(1L);

        assertThat(user.getAccount()).isEqualTo("gugu");
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

        final var actual = userDao.findByAccount(account);

        assertThat(actual.getAccount()).isEqualTo(account);
    }

    @Test
    void update() {
        final var newPassword = "password99";
        final var user = userDao.findById(1L);
        user.changePassword(newPassword);

        userDao.update(user);

        final var actual = userDao.findById(1L);

        assertThat(actual.getPassword()).isEqualTo(newPassword);
    }
}
