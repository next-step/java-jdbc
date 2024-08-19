package camp.nextstep.transaction;

import camp.nextstep.transaction.support.TransactionException;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataSourceTransactionManagerTest {

    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

    @AfterEach
    void tearDown() {
        TransactionSynchronizationManager.unbindResource(dataSource);
    }

    @Test
    @DisplayName("트랜잭션이 시작되면 새로운 Connection 이 생성되고 AutoCommit 이 false로 설정된다.")
    void getTransactionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        transactionManager.getTransaction();

        verify(dataSource).getConnection();
        verify(connection).setAutoCommit(false);
        assertThat(TransactionSynchronizationManager.getResource(dataSource).getConnection()).isSameAs(connection);
    }

    @Test
    @DisplayName("트랜잭션이 시작된 후 커밋을 호출하면 Connection 이 커밋되고 닫힌다.")
    void commitTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        transactionManager.getTransaction();

        transactionManager.commit();

        verify(connection).commit();
        verify(connection).close();
    }

    @Test
    @DisplayName("트랜잭션이 시작된 후 롤백을 호출하면 Connection 이 롤백되고 닫힌다.")
    void rollbackTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        transactionManager.getTransaction();

        transactionManager.rollback();

        verify(connection).rollback();
        verify(connection).close();
    }

    @Test
    @DisplayName("트랜잭션이 없는 상태에서 커밋을 호출하면 예외가 발생한다.")
    void commitWithoutTransactionTest() {
        assertThatThrownBy(transactionManager::commit)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("트랜잭션이 없는 상태에서 롤백을 호출하면 예외가 발생한다.")
    void rollbackWithoutTransactionTest() {
        assertThatThrownBy(transactionManager::rollback)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("커넥션을 가져올때 예외가 발생하면 TransactionException 이 던져진다.")
    void getTransactionExceptionTest() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException());

        assertThatThrownBy(transactionManager::getTransaction)
                .isInstanceOf(TransactionException.class);
    }

    @Test
    @DisplayName("커밋 시 예외가 발생하면 TransactionException 이 던져진다.")
    void commitExceptionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        doThrow(new SQLException()).when(connection).commit();
        transactionManager.getTransaction();

        assertThatThrownBy(transactionManager::commit)
                .isInstanceOf(TransactionException.class);
    }

    @Test
    @DisplayName("롤백 시 예외가 발생하면 TransactionException 이 던져진다.")
    void rollbackExceptionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        doThrow(new SQLException()).when(connection).rollback();
        transactionManager.getTransaction();

        assertThatThrownBy(transactionManager::rollback)
                .isInstanceOf(TransactionException.class);
    }
}
