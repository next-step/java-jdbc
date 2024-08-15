package camp.nextstep.jdbc.core;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ConnectionManagerTest {

    private DataSource dataSource = dataSource();

    @Test
    @DisplayName("ConnectionManager는 첫 번째 요청에서 새로운 Connection을 반환해야 한다")
    void shouldReturnNewConnectionOnFirstRequest() throws Exception {
        // when
        final Connection connection = ConnectionManager.getConnection(dataSource);

        // then
        assertThat(connection).isNotNull();
    }

    @Test
    @DisplayName("ConnectionManager는 동일한 스레드 내에서 같은 Connection을 반환해야 한다")
    void shouldReturnSameConnectionWithinSameThread() throws Exception {
        // given
        final Connection connection1 = ConnectionManager.getConnection(dataSource);

        // when
        final Connection connection2 = ConnectionManager.getConnection(dataSource);

        // then
        assertThat(connection1).isSameAs(connection2);
    }

    @Test
    @DisplayName("ConnectionManager는 Connection이 닫힌 후에는 새로운 Connection을 반환해야 한다")
    void shouldReturnNewConnectionAfterClosedConnection() throws Exception {
        // given
        final Connection connection1 = ConnectionManager.getConnection(dataSource);
        connection1.close();

        // when
        final Connection connection2 = ConnectionManager.getConnection(dataSource);

        // then
        assertThat(connection2).isNotSameAs(connection1);
    }

    @Test
    @DisplayName("ConnectionManager는 다른 스레드에서는 다른 Connection을 반환해야 한다")
    void shouldReturnDifferentConnectionInDifferentThread() throws InterruptedException {
        // given
        final Connection[] connections = new Connection[2];

        // when
        final Thread thread1 = new Thread(() -> {
            try {
                connections[0] = ConnectionManager.getConnection(dataSource);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                connections[1] = ConnectionManager.getConnection(dataSource);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // then
        assertAll(
                () -> assertThat(connections[0]).isNotNull(),
                () -> assertThat(connections[1]).isNotNull(),
                () -> assertThat(connections[0]).isNotSameAs(connections[1])
        );
    }

    private DataSource dataSource() {
        final var jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
        jdbcDataSource.setUser("");
        jdbcDataSource.setPassword("");
        return jdbcDataSource;
    }
}
