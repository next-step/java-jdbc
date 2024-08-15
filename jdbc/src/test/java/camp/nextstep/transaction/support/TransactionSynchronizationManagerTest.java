package camp.nextstep.transaction.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.sql.Connection;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransactionSynchronizationManagerTest {
    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
        dataSource.setUser("");
        dataSource.setPassword("");
    }

    @DisplayName("Connection을 보관한다.")
    @Test
    void bindResource() throws SQLException {
        Connection connection = dataSource.getConnection();
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        Connection storedConnection = TransactionSynchronizationManager.getResource(dataSource);

        assertAll(
                () -> assertThat(storedConnection).isNotNull(),
                () -> assertThat(storedConnection).isSameAs(connection)
        );
    }

    @DisplayName("이미 보관 중인 Connection이 있을 경우, 교체한다.")
    @Test
    void bindResource2() throws SQLException {
        Connection connection1 = dataSource.getConnection();
        TransactionSynchronizationManager.bindResource(dataSource, connection1);

        Connection connection2 = dataSource.getConnection();
        TransactionSynchronizationManager.bindResource(dataSource, connection2);

        Connection storedConnection = TransactionSynchronizationManager.getResource(dataSource);

        assertAll(
                () -> assertThat(storedConnection).isNotNull(),
                () -> assertThat(storedConnection).isNotSameAs(connection1),
                () -> assertThat(storedConnection).isSameAs(connection2)
        );
    }

    @DisplayName("이미 보관 중인 Connection이 있을 경우 활성화된 트랜잭션이 있는 것으로 보아 true를 반환한다.")
    @Test
    void isTransactionActive() throws SQLException {
        TransactionSynchronizationManager.bindResource(dataSource, dataSource.getConnection());

        boolean transactionActive = TransactionSynchronizationManager.isTransactionActive(dataSource);

        assertThat(transactionActive).isTrue();
    }

    @DisplayName("보관 중인 Connection을 반환한다.")
    @Test
    void getResource() throws SQLException {
        TransactionSynchronizationManager.bindResource(dataSource, dataSource.getConnection());

        Connection storedConnection = TransactionSynchronizationManager.getResource(dataSource);

        assertThat(storedConnection).isNotNull();
    }

    @DisplayName("보관 중인 Connection이 없으면 null을 반환한다.")
    @Test
    void getResource2() {
        Connection storedConnection = TransactionSynchronizationManager.getResource(dataSource);

        assertThat(storedConnection).isNull();
    }

    @DisplayName("할당된 리소스를 제거하고, 제거 전의 Connection을 반환한다.")
    @Test
    void unbindResource() throws SQLException {
        Connection connectionBeforeStore = dataSource.getConnection();
        TransactionSynchronizationManager.bindResource(dataSource, connectionBeforeStore);

        Connection storedConnection = TransactionSynchronizationManager.unbindResource(dataSource);
        assertThat(storedConnection).isSameAs(connectionBeforeStore);

        Connection connection = TransactionSynchronizationManager.getResource(dataSource);
        assertThat(connection).isNull();
    }
}