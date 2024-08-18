package camp.nextstep.transaction.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionSynchronizationManagerTest {

    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);

    @AfterEach
    void tearDown() {
        TransactionSynchronizationManager.unbindResource(dataSource);
    }

    @Test
    @DisplayName("같은 스레드에서 같은 DataSource 에 대해 동일한 Connection 객체를 반환한다.")
    void getResourceWithSameThreadTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        final Connection firstConnection = TransactionSynchronizationManager.getResource(dataSource);
        final Connection secondConnection = TransactionSynchronizationManager.getResource(dataSource);

        assertThat(firstConnection).isSameAs(secondConnection);
    }

    @Test
    @DisplayName("다른 DataSource 에 대해서는 다른 Connection 객체를 반환한다.")
    void getResourceWithDiffConnectionTest() throws SQLException {
        final DataSource anotherDataSource = mock(DataSource.class);
        final Connection anotherConnection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(anotherDataSource.getConnection()).thenReturn(anotherConnection);
        TransactionSynchronizationManager.bindResource(dataSource, connection);
        TransactionSynchronizationManager.bindResource(anotherDataSource, anotherConnection);

        final Connection firstConnection = TransactionSynchronizationManager.getResource(dataSource);
        final Connection secondConnection = TransactionSynchronizationManager.getResource(anotherDataSource);

        assertThat(firstConnection).isNotSameAs(secondConnection);
    }


    @Test
    @DisplayName("다른 스레드에서 같은 DataSource 에 대해 다른 Connection 객체를 반환한다.")
    void getResourceWithDiffThreadTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        final Connection firstConnection = TransactionSynchronizationManager.getResource(dataSource);

        new Thread(RunnableWrapper.accept(() -> {
            final Connection anotherConnection = mock(Connection.class);
            when(dataSource.getConnection()).thenReturn(anotherConnection);
            TransactionSynchronizationManager.bindResource(dataSource, anotherConnection);

            final Connection secondConnection = TransactionSynchronizationManager.getResource(dataSource);

            assertThat(firstConnection).isNotSameAs(secondConnection);
        })).start();
    }

    @Test
    @DisplayName("Connection 을 바인딩 할 수 있다.")
    void bindResourceTest() {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        final Connection boundConnection = TransactionSynchronizationManager.getResource(dataSource);

        assertThat(boundConnection).isSameAs(connection);
    }

    @Test
    @DisplayName("이미 Connection 이 바인딩 되어 있는데 bind 시도 시 예외를 던진다.")
    void alreadyBindResourceTest() {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        assertThatThrownBy(() -> TransactionSynchronizationManager.bindResource(dataSource, connection))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Connection 을 언바인딩할 수 있다.")
    void unbindResourceTest() {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        final Connection unboundConnection = TransactionSynchronizationManager.unbindResource(dataSource);

        assertSoftly(softly -> {
            softly.assertThat(unboundConnection).isSameAs(connection);
            softly.assertThat(TransactionSynchronizationManager.getResource(dataSource)).isNull();
        });
    }

    @Test
    @DisplayName("getConnection 이 새로운 Connection 을 생성하고 반환한다.")
    void getConnectionCreatesNewConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        TransactionSynchronizationManager.getConnection(dataSource);

        verify(dataSource, times(1)).getConnection();
    }

    @Test
    @DisplayName("getConnection 이 이미 바인딩된 Connection 을 반환한다.")
    void getConnectionReturnsBoundConnectionTest() throws SQLException {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        final Connection retrievedConnection = TransactionSynchronizationManager.getConnection(dataSource);

        assertThat(retrievedConnection).isSameAs(connection);
        verify(dataSource, never()).getConnection();
    }

    @Test
    @DisplayName("tryToCloseConnection 이 바인딩되지 않은 Connection 을 닫는다.")
    void tryToCloseConnectionClosesUnboundConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        final Connection newConnection = TransactionSynchronizationManager.getConnection(dataSource);

        TransactionSynchronizationManager.tryToCloseConnection(newConnection, dataSource);

        verify(newConnection, times(1)).close();
    }

    @Test
    @DisplayName("tryToCloseConnection 이 바인딩된 Connection 은 닫지 않는다.")
    void tryToCloseConnectionDoesNotCloseBoundConnectionTest() throws SQLException {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        TransactionSynchronizationManager.tryToCloseConnection(connection, dataSource);

        verify(connection, never()).close();
    }
}
