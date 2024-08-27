package camp.nextstep.transaction.support;

import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.dao.UserDao;
import camp.nextstep.jdbc.core.JdbcTemplate;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransactionSynchronizationManagerTest {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        final var myConfiguration = new MyConfiguration();
        dataSource = myConfiguration.dataSource();

    }

    @Test
    @DisplayName("Connection을 제거한다.")
    void unbindResource() throws SQLException {
        Connection existing = dataSource.getConnection();
        TransactionSynchronizationManager.bindResource(dataSource, existing);

        Connection connection = TransactionSynchronizationManager.unbindResource(dataSource);

        assertThat(TransactionSynchronizationManager.getResource(dataSource)).isNull();
    }

    @Test
    @DisplayName("이미 있는 Connection을 반환한다.")
    void returnExistingResource() throws SQLException {
        Connection existing = dataSource.getConnection();
        TransactionSynchronizationManager.bindResource(dataSource, existing);

        Connection connection = TransactionSynchronizationManager.getResource(dataSource);

        assertThat(connection).isEqualTo(connection);
    }
}
