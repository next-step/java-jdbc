package camp.nextstep.jdbc.datasource;

import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataSourceUtilsTest {

    @Test
    void connection이_최초_생성인_경우_새_connection을_바인딩하여_가져온다() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);

        Connection actual = DataSourceUtils.getConnection(dataSource);
        assertAll(
                () -> assertThat(actual).isEqualTo(connection),
                () -> assertThat(TransactionSynchronizationManager.findResource(dataSource)).isEqualTo(Optional.of(connection))
        );
    }
}
