package camp.nextstep.jdbc.datasource;

import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void tearDown() {
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    @Test
    @DisplayName("getConnection 이 새로운 Connection 을 생성하고 반환한다.")
    void getConnectionCreatesNewConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        DataSourceUtils.getConnection(dataSource);

        verify(dataSource, times(1)).getConnection();
    }

    @Test
    @DisplayName("release 되지 않았다면 getConnection 이 같은 Connection 을 반환한다.")
    void getConnectionReturnsBoundConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        final Connection firstConnection = DataSourceUtils.getConnection(dataSource);
        final Connection secondConnection = DataSourceUtils.getConnection(dataSource);

        assertThat(secondConnection).isSameAs(firstConnection);
    }

    @Test
    @DisplayName("releaseConnection 이 바인딩되지 않은 Connection 을 닫는다.")
    void releaseConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        DataSourceUtils.releaseConnection(connection, dataSource);

        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("releaseConnection 이 한번 참조 된 Connection 을 닫는다.")
    void releaseConnectionOneTimeBoundConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        final Connection firstConnection = DataSourceUtils.getConnection(dataSource);
        DataSourceUtils.releaseConnection(firstConnection, dataSource);

        verify(connection, times(1)).close();
    }

    @Test
    @DisplayName("releaseConnection 이 두번 이상 참조 된 Connection 은 닫지 않는다.")
    void releaseConnectionMoreThanOneTimesBoundConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        final Connection firstConnection = DataSourceUtils.getConnection(dataSource);
        final Connection secondConnection = DataSourceUtils.getConnection(dataSource);

        DataSourceUtils.releaseConnection(secondConnection, dataSource);

        verify(connection, never()).close();
    }

    @Test
    @DisplayName("releaseConnection 이 참조 된 횟수만큼 호출 되면 Connection 은 닫는다.")
    void releaseConnectionSameTimesBoundConnectionTest() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        final Connection firstConnection = DataSourceUtils.getConnection(dataSource);
        final Connection secondConnection = DataSourceUtils.getConnection(dataSource);

        DataSourceUtils.releaseConnection(firstConnection, dataSource);
        DataSourceUtils.releaseConnection(secondConnection, dataSource);

        verify(connection, times(1)).close();
    }
}
