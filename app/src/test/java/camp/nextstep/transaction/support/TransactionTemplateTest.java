package camp.nextstep.transaction.support;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicReference;

class TransactionTemplateTest {
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        final var myConfiguration = new MyConfiguration();
        dataSource = myConfiguration.dataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        DatabasePopulatorUtils.execute(dataSource);
        jdbcTemplate.update("insert into users(account, password, email) values('new-account', 'password', 'abc@example.com')");
    }

    @Test
    @DisplayName("동일한 동일한 transactionTemplate 에서는 동일한 Connection 을 사용한다.")
    void useSameConnectionInOneTransactionTemplate() {
        TransactionManager transactionManager = new TransactionManager(dataSource);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        AtomicReference<Connection> first = new AtomicReference<>();
        AtomicReference<Connection> second = new AtomicReference<>();
        transactionTemplate.execute(status -> {
            first.set(status.connection());
            second.set(status.connection());
            return null;
        });

        assertEquals(first.get(), second.get());
    }

    @Test
    @DisplayName("서로 다른 transactionTemplate 에서는 다른 Connection 을 사용한다.")
    void transactionTemplatesUseEachConnection() {
        TransactionManager transactionManager = new TransactionManager(dataSource);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        AtomicReference<Connection> first = new AtomicReference<>();
        AtomicReference<Connection> second = new AtomicReference<>();
        transactionTemplate.execute(status -> {
            first.set(status.connection());
            return null;
        });

        transactionTemplate.execute(status -> {
            second.set(status.connection());
            return null;
        });

        assertNotEquals(first.get(), second.get());
    }
}
