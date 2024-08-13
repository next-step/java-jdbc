package camp.nextstep.service;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.dao.DataAccessException;
import camp.nextstep.dao.UserDao;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.jdbc.transaction.TransactionManager;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionUserServiceTest {

    private TransactionManager transactionManager;
    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        final var myConfiguration = new MyConfiguration();
        DataSource dataSource = myConfiguration.dataSource();
        this.transactionManager = new TransactionManager(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.userDao = new UserDao(jdbcTemplate);

        DatabasePopulatorUtils.execute(dataSource);
    }

    @Test
    void testChangePassword() {
        final var userHistoryDao = new UserHistoryDao(jdbcTemplate);
        final var appUserService = new AppUserService(userDao, userHistoryDao);
        final var txUserService = new TransactionUserService(appUserService, transactionManager);

        final var newPassword = "qqqqq";
        final var createBy = "gugu";
        txUserService.changePassword(1L, newPassword, createBy);

        final var actual = txUserService.findById(1L);

        assertThat(actual.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void testTransactionRollback() {
        // 트랜잭션 롤백 테스트를 위해 mock으로 교체
        final var userHistoryDao = new MockUserHistoryDao(jdbcTemplate);
        final var appUserService = new AppUserService(userDao, userHistoryDao);
        final var txUserService = new TransactionUserService(appUserService, transactionManager);

        final var newPassword = "newPassword";
        final var createBy = "gugu";
        // 트랜잭션이 정상 동작하는지 확인하기 위해 의도적으로 MockUserHistoryDao에서 예외를 발생시킨다.
        assertThrows(DataAccessException.class,
            () -> txUserService.changePassword(1L, newPassword, createBy));

        final var actual = txUserService.findById(1L);

        assertThat(actual.getPassword()).isNotEqualTo(newPassword);
    }
}
