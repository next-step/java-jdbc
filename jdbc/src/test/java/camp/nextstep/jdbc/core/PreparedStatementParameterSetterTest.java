package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("PreparedStatementParameterSetter 클래스의 ")
class PreparedStatementParameterSetterTest {

    private PreparedStatement preparedStatement;

    @BeforeEach
    public void setUp() {
        preparedStatement = mock(PreparedStatement.class);
    }

    @DisplayName("setParameter 메서드는")
    @Nested
    class SetParameter {

        @DisplayName("문자열 타입의 값을 설정한다.")
        @Test
        void testSetStringParameter() throws SQLException {
            String value = "testString";
            PreparedStatementParameterSetter.setParameter(preparedStatement, 1, value);

            verify(preparedStatement).setString(1, value);
        }

        @DisplayName("long 타입의 값을 설정한다.")
        @Test
        void testSetLongParameter() throws SQLException {
            long value = 123L;
            PreparedStatementParameterSetter.setParameter(preparedStatement, 2, value);

            verify(preparedStatement).setLong(2, value);
        }

        @DisplayName("int 타입의 값을 설정한다.")
        @Test
        void testSetIntegerParameter() throws SQLException {
            int value = 456;
            PreparedStatementParameterSetter.setParameter(preparedStatement, 3, value);

            verify(preparedStatement).setInt(3, value);
        }

        @DisplayName("boolean 타입의 값을 설정한다.")
        @Test
        void testSetBooleanParameter() throws SQLException {
            boolean value = true;
            PreparedStatementParameterSetter.setParameter(preparedStatement, 4, value);

            verify(preparedStatement).setBoolean(4, value);
        }

        @DisplayName("Object 타입의 값을 설정한다.")
        @Test
        void testSetObjectParameter() throws SQLException {
            Object value = new Object();
            PreparedStatementParameterSetter.setParameter(preparedStatement, 5, value);

            verify(preparedStatement).setObject(5, value);
        }

        @DisplayName("알 수 없는 타입의 값은 Object로 설정한다.")
        @Test
        void testSetParameterWithUnknownType() throws SQLException {
            LocalDateTime value = LocalDateTime.now();
            PreparedStatementParameterSetter.setParameter(preparedStatement, 6, value);

            verify(preparedStatement).setObject(6, value);
        }
    }
}
