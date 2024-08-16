package camp.nextstep.transaction.support;

import camp.nextstep.jdbc.core.JdbcException;
import camp.nextstep.jdbc.support.DataSourceBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TransactionSynchronizationManagerTest {

    public static final DataSource dataSource = DataSourceBean.dataSource();

    @AfterEach
    void tearDown() {
        final Connection connection = TransactionSynchronizationManager.getResource(dataSource);

        if (connection != null) {
            TransactionSynchronizationManager.unbindResource(dataSource);
        }
    }

    @Test
    @DisplayName("스레드에 만들어진 Connection 이 없다면 null 을 반환 한다.")
    void getResource_null() throws Exception {
        // when
        final Connection connection = TransactionSynchronizationManager.getResource(dataSource);

        // then
        assertThat(connection).isNull();
    }

    @DisplayName("스레드 별 새로운 Connection 을 저장 한다")
    @Test
    public void bindResource() throws Exception {
        // when then
        assertDoesNotThrow(() -> TransactionSynchronizationManager.bindResource(dataSource, dataSource.getConnection()));
    }

    @Test
    @DisplayName("bind 후에는 동일한 스레드 내에서 같은 Connection을 반환해야 한다")
    void getResource_connection() throws Exception {
        // given
        TransactionSynchronizationManager.bindResource(dataSource, dataSource.getConnection());

        // when
        final Connection actual1 = TransactionSynchronizationManager.getResource(dataSource);
        final Connection actual2 = TransactionSynchronizationManager.getResource(dataSource);

        // then

        assertAll(
                () -> assertThat(actual1).isNotNull(),
                () -> assertThat(actual2).isNotNull(),
                () -> assertThat(actual1).isSameAs(actual2)
        );
    }

    @Test
    @DisplayName("bind 된 후에는 다른 스레드에서 다른 Connection을 반환해야 한다")
    void getResource_diffThread() throws Exception {
        // given
        final Connection[] connections = new Connection[2];

        // when
        final Thread thread1 = new Thread(() -> {
            try {
                TransactionSynchronizationManager.bindResource(dataSource, dataSource.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            connections[0] = TransactionSynchronizationManager.getResource(dataSource);
        });

        final Thread thread2 = new Thread(() -> {
            try {
                TransactionSynchronizationManager.bindResource(dataSource, dataSource.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            connections[1] = TransactionSynchronizationManager.getResource(dataSource);
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // then
        assertAll(
                () -> assertThat(connections[0]).isNotNull(),
                () -> assertThat(connections[1]).isNotNull(),
                () -> assertThat(connections[0]).isNotSameAs(connections[1])
        );
    }

    @DisplayName("스레드 별 저장된 Connection 을 삭제 한다")
    @Test
    public void unbindResource() throws Exception {
        // given
        TransactionSynchronizationManager.bindResource(dataSource, dataSource.getConnection());

        // when
        TransactionSynchronizationManager.unbindResource(dataSource);

        // then
        final Connection actual = TransactionSynchronizationManager.getResource(dataSource);
        assertThat(actual).isNull();
    }

    @DisplayName("스레드 별 저장된 Connection 을 삭제 할 때, 저장된 Connection 이 없다면 예외를 던진다")
    @Test
    public void unbindResource_exception() throws Exception {
        // when then
        assertThatThrownBy(() -> TransactionSynchronizationManager.unbindResource(dataSource))
                .isInstanceOf(JdbcException.class);
    }
}
