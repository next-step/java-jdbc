package camp.nextstep.jdbc.core;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultPreparedStatementSetterTest {
    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);

    @Test
    @DisplayName("PreparedStatemenetSetter로 쿼리 조립을 확인합니다.")
    void prepareStatementSeter() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        DefaultPreparedStatementSetter setter = new DefaultPreparedStatementSetter();

        doNothing().when(preparedStatement).setObject(1, "hello");
        doNothing().when(preparedStatement).setObject(2, "hi");
        doNothing().when(preparedStatement).setObject(3, "uwelcome");

        setter.setValues(preparedStatement, "hello", "hi", "uwelcome");

        verify(preparedStatement).setObject(1, "hello");
        verify(preparedStatement).setObject(2, "hi");
        verify(preparedStatement).setObject(3, "uwelcome");
    }
}
