package camp.nextstep.transaction.support;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConnectionHolderTest {
    private final Connection connection = mock(Connection.class);

    @Test
    @DisplayName("ConnectionHolder 가 초기화되면 isOpen 은 true 를 반환한다.")
    void initTest() {
        final ConnectionHolder connectionHolder = new ConnectionHolder(connection);

        assertThat(connectionHolder.isOpen()).isTrue();
    }

    @Test
    @DisplayName("referenceCount 가 0이 되면 isOpen 메서드가 false 를 반환한다.")
    void isOpenTest() {
        final ConnectionHolder connectionHolder = new ConnectionHolder(connection);

        connectionHolder.released();

        assertThat(connectionHolder.isOpen()).isFalse();
    }

    @Test
    @DisplayName("getConnection 은 생성 시 넘긴 Connection 객체를 반환한다.")
    void getConnectionTest() {
        final ConnectionHolder connectionHolder = new ConnectionHolder(connection);

        assertThat(connectionHolder.getConnection()).isSameAs(connection);
    }
}
