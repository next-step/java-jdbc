package camp.nextstep.transaction.support;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TransactionSynchronizationManagerTest {

    @Test
    void 요청된_DataSource에_connection이_없는_경우_empty를_반환한다() {
        DataSource dataSource = mock(DataSource.class);
        Optional<Connection> actual = TransactionSynchronizationManager.findResource(dataSource);
        assertThat(actual).isEmpty();
    }
}
