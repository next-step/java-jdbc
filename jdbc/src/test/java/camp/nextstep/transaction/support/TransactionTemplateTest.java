package camp.nextstep.transaction.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionTemplateTest {

    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        transactionTemplate = new TransactionTemplate(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    @DisplayName("메서드가 정상적으로 실행 되고 트랜잭션이 commit 된다.")
    void runWithoutReturnTest() throws SQLException {
        final RunnableClass runnableClass = new RunnableClass();

        transactionTemplate.run(runnableClass);

        assertThat(runnableClass.counter).isEqualTo(1);
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).close();
    }

    private static class RunnableClass implements Runnable {
        private int counter;

        @Override
        public void run() {
            counter++;
        }
    }

    @Test
    @DisplayName("메서드가 정상적으로 실행 되어 값을 반환 하고 트랜잭션이 commit 된다.")
    void runWithReturnTest() throws SQLException {
        final String result = transactionTemplate.run(() -> "success");

        assertThat(result).isEqualTo("success");
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).close();
    }

    @Test
    @DisplayName("메서드 실행 중 예외가 발생하면 롤백이 실행된다.")
    void runExceptionTest() throws SQLException {
        assertThatThrownBy(() ->
                transactionTemplate.run(() -> {
                    throw new RuntimeException();
                }))
                .isInstanceOf(RuntimeException.class);

        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verify(connection).close();
    }

    @Test
    @DisplayName("template 내부에서는 기존 커넥션을 재사용한다.")
    void connectionReusedTest() throws SQLException {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        transactionTemplate.run(() -> {
            final Connection resource = TransactionSynchronizationManager.getResource(dataSource);
            assertThat(resource).isSameAs(connection);
        });

        verify(connection).commit();
        verify(connection).close();
    }

    @Test
    @DisplayName("template 내부 실행 뒤에는 unbind 된다.")
    void connectionUnbindAfterTemplateTest() throws SQLException {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        transactionTemplate.run(() -> {
            final Connection resource = TransactionSynchronizationManager.getResource(dataSource);
            assertThat(resource).isNotNull();
        });

        verify(connection).commit();
        verify(connection).close();

        final Connection resource = TransactionSynchronizationManager.getResource(dataSource);
        assertThat(resource).isNull();
    }

}
