package camp.nextstep.transaction.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.mock;
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
    void getResourceWithDiffThreadTest() throws SQLException {
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
    @DisplayName("Connection 을 바인딩 할 수 있다.")
    void bindResourceTest() {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        final Connection boundConnection = TransactionSynchronizationManager.getResource(dataSource);

        assertThat(boundConnection).isSameAs(connection);
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

}
