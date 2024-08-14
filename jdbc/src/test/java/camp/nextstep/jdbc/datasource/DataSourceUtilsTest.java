package camp.nextstep.jdbc.datasource;

import camp.nextstep.jdbc.transaction.TransactionSynchronizationManager;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class DataSourceUtilsTest {

    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);

    @Test
    void connection이_최초_생성인_경우_새_connection을_바인딩하여_가져온다() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        Connection actual = DataSourceUtils.getConnection(dataSource);
        assertAll(
                () -> assertThat(actual).isEqualTo(connection),
                () -> assertThat(TransactionSynchronizationManager.findResource(dataSource)).isEqualTo(Optional.of(connection))
        );
    }

    @Test
    void 요청된_DataSource에_해당하는_데이터를_unbind한다() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        doNothing().when(connection).close();
        DataSourceUtils.getConnection(dataSource);

        DataSourceUtils.releaseConnection(dataSource);
        verify(connection).close();
    }
}
