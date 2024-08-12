package camp.nextstep.jdbc.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IndexedPreparedStatementSetterTest {

    @Test
    @DisplayName("index 기반으로 prepareStatement 에 값을 셋팅할 수 있다.")
    void testIndexedPrepareStatementSetter() throws SQLException {
        final Object[] values = {1, "test", "password", "test@test.com"};
        final IndexedPreparedStatementSetter setter = new IndexedPreparedStatementSetter(values);
        final PreparedStatement preparedStatement = mock(PreparedStatement.class);

        setter.setValues(preparedStatement);

        verify(preparedStatement).setObject(1, 1);
        verify(preparedStatement).setObject(2, "test");
        verify(preparedStatement).setObject(3, "password");
        verify(preparedStatement).setObject(4, "test@test.com");
    }
}
