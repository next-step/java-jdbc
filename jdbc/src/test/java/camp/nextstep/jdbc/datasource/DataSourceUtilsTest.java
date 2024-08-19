package camp.nextstep.jdbc.datasource;

import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataSourceUtilsTest {
    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);

    @Test
    @DisplayName("getConnection 이 새로운 Connection 을 생성하고 반환한다.")
    void getConnectionCreatesNewConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        DataSourceUtils.getConnection(dataSource);

        verify(dataSource, times(1)).getConnection();
    }

    @Test
    @DisplayName("getConnection 이 이미 바인딩된 Connection 을 반환한다.")
    void getConnectionReturnsBoundConnectionTest() throws SQLException {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        final Connection retrievedConnection = DataSourceUtils.getConnection(dataSource);

        assertThat(retrievedConnection).isSameAs(connection);
        verify(dataSource, never()).getConnection();
    }

    @Test
    @DisplayName("tryToCloseConnection 이 바인딩되지 않은 Connection 을 닫는다.")
    void tryToCloseConnectionClosesUnboundConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        final Connection newConnection = DataSourceUtils.getConnection(dataSource);

        DataSourceUtils.releaseConnection(newConnection, dataSource);

        verify(newConnection, times(1)).close();
    }

    @Test
    @DisplayName("tryToCloseConnection 이 바인딩된 Connection 은 닫지 않는다.")
    void tryToCloseConnectionDoesNotCloseBoundConnectionTest() throws SQLException {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        DataSourceUtils.releaseConnection(connection, dataSource);

        verify(connection, never()).close();
    }
}
