package camp.nextstep.service;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.dao.DataAccessException;
import camp.nextstep.dao.UserDao;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

class UserServiceTest {

    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        final var myConfiguration = new MyConfiguration();
        dataSource = myConfiguration.dataSource();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.userDao = new UserDao(jdbcTemplate);

        DatabasePopulatorUtils.execute(dataSource);
    }

    @Test
    @DisplayName("트랜잭션을 사용하는 changePassword() 가 잘 동작한다")
    void testChangePassword() {
        final var userHistoryDao = new UserHistoryDao(jdbcTemplate);
        final var appUserService = new AppUserService(userDao, userHistoryDao);
        final var userService = new TxUserService(appUserService, dataSource);

        final var newPassword = "qqqqq";
        final var createBy = "gugu";
        userService.changePassword(1L, newPassword, createBy);

        final var actual = userService.findById(1L);

        assertThat(actual.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("트랜잭션 내에서 예외가 발생하면 롤백된다.")
    void testTransactionRollback() {
        // 트랜잭션 롤백 테스트를 위해 mock으로 교체
        final var userHistoryDao = new MockUserHistoryDao(jdbcTemplate);

        final var appUserService = new AppUserService(userDao, userHistoryDao);
        final var userService = new TxUserService(appUserService, dataSource);

        final var newPassword = "newPassword";
        final var createdBy = "gugu";

        // 트랜잭션이 정상 동작하는지 확인하기 위해 의도적으로 MockUserHistoryDao에서 예외를 발생시킨다.
        assertThrows(DataAccessException.class,
                     () -> userService.changePassword(1L, newPassword, createdBy));

        final var actual = userService.findById(1L);

        assertThat(actual.getPassword()).isNotEqualTo(newPassword);
    }

    @Test
    @DisplayName("트랜잭션이 없어도 save 메서드가 정상적으로 동작하는지 확인한다.")
    void testSave() {
        final var appUserService = new AppUserService(userDao, null);
        final var userService = new TxUserService(appUserService, dataSource);

        String givenNewAccount = "new-account";
        assertThat(userService.findByAccount(givenNewAccount)).isNull();

        userService.save(new User(givenNewAccount, "new-password", "new-email"));

        final var actual = userService.findByAccount(givenNewAccount);
        assertThat(actual).isNotNull()
                .hasFieldOrPropertyWithValue("account", givenNewAccount);
    }
}
