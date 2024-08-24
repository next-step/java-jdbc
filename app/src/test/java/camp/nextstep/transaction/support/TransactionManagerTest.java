package camp.nextstep.transaction.support;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;

class TransactionManagerTest {
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
    @DisplayName("동일한 TransactionManager 에서는 동일한 Connection 을 사용한다.")
    void useSameConnectionInOneTransactionSection() {
        TransactionManager transactionManager = new TransactionManager(dataSource);
        TransactionStatus first = transactionManager.getTransaction();
        Connection firstConnection = first.connection();
        Connection secondConnection = first.connection();

        assertThat(firstConnection).isEqualTo(secondConnection);
    }

    @Test
    @DisplayName("커밋이 이뤄진 다음 트랜잭션에서는 다른 Connection 을 사용한다.")
    void transactionSectionsUseEachConnection() {
        TransactionManager transactionManager = new TransactionManager(dataSource);
        TransactionStatus first = transactionManager.getTransaction();
        Connection firstConnection = first.connection();
        transactionManager.commit(first);

        TransactionStatus second = transactionManager.getTransaction();
        Connection secondConnection = second.connection();

        assertThat(firstConnection).isNotEqualTo(secondConnection);
    }
}
