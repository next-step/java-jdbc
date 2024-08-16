package camp.nextstep.service;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.dao.DataAccessException;
import camp.nextstep.dao.UserDao;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static support.CleanUp.cleanUp;

class UserServiceTest {

    final static MyConfiguration myConfiguration = new MyConfiguration();
    private static final DataSource dataSource = myConfiguration.dataSource();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    private final UserDao userDao = new UserDao(jdbcTemplate);

    @BeforeAll
    static void beforeAll() {
        DatabasePopulatorUtils.execute(dataSource);
    }

    @AfterEach
    void tearDown() {
        cleanUp(dataSource);
    }

    @Test
    void testChangePassword() {
        final var userHistoryDao = new UserHistoryDao(jdbcTemplate);
        final var appUserService = new AppUserService(userDao, userHistoryDao);
        final TxUserService userService = new TxUserService(appUserService, dataSource);

        final var user = new User("gugu", "password", "hkkang@woowahan.com");
        userDao.insert(user);

        final var newPassword = "qqqqq";
        final var createBy = "gugu";
        userService.changePassword(1L, newPassword, createBy);

        final var actual = userService.findById(1L);

        assertThat(actual.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void testTransactionRollback() {
        // 트랜잭션 롤백 테스트를 위해 mock으로 교체
        final var userHistoryDao = new MockUserHistoryDao(jdbcTemplate);
        final var appUserService = new AppUserService(userDao, userHistoryDao);
        final TxUserService userService = new TxUserService(appUserService, dataSource);

        final var user = new User("gugu", "password", "hkkang@woowahan.com");
        userDao.insert(user);

        final var newPassword = "newPassword";
        final var createBy = "gugu";
        // 트랜잭션이 정상 동작하는지 확인하기 위해 의도적으로 MockUserHistoryDao에서 예외를 발생시킨다.
        assertThrows(DataAccessException.class,
            () -> userService.changePassword(1L, newPassword, createBy));

        final var actual = userService.findById(1L);

        assertThat(actual.getPassword()).isNotEqualTo(newPassword);
    }
}
