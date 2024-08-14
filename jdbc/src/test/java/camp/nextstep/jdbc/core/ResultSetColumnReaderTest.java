package camp.nextstep.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ResultSetColumnReader 클래스의")
class ResultSetColumnReaderTest {

    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        resultSet = mock(ResultSet.class);
    }

    @DisplayName("read 메서드는")
    @Nested
    class Read {

        @DisplayName("ResultSet을 이용하여 문자열 필드를 매핑한다.")
        @Test
        void testMapString() throws SQLException {
            // Given
            when(resultSet.getString("name")).thenReturn("gugu");

            // When
            String result = (String) ResultSetColumnReader.read(resultSet, "name", String.class);

            // Then
            assertEquals("gugu", result);
        }

        @DisplayName("ResultSet을 이용하여 long 필드를 매핑한다.")
        @Test
        void testMapLong() throws SQLException {
            // Given
            when(resultSet.getLong("id")).thenReturn(1L);

            // When
            Long result = (Long) ResultSetColumnReader.read(resultSet, "id", Long.class);

            // Then
            assertEquals(1L, result);
        }

        @DisplayName("ResultSet을 이용하여 int 필드를 매핑한다.")
        @Test
        void testMapInteger() throws SQLException {
            // Given
            when(resultSet.getInt("age")).thenReturn(20);

            // When
            int result = (int) ResultSetColumnReader.read(resultSet, "age", int.class);

            // Then
            assertEquals(20, result);
        }

        @DisplayName("ResultSet을 이용하여 boolean 필드를 매핑한다.")
        @Test
        void testMapBoolean() throws SQLException {
            // Given
            when(resultSet.getBoolean("active")).thenReturn(true);

            // When
            Boolean result = (Boolean) ResultSetColumnReader.read(resultSet, "active", Boolean.class);

            // Then
            assertTrue(result);
        }

        @DisplayName("ResultSet을 이용하여 Object 필드를 매핑한다.")
        @Test
        void testMapObject() throws SQLException {
            // Given
            Object expected = new Object();
            when(resultSet.getObject("data")).thenReturn(expected);

            // When
            Object result = ResultSetColumnReader.read(resultSet, "data", Object.class);

            // Then
            assertSame(expected, result);
        }

        @DisplayName("알 수 없는 타입의 필드를 매핑하면 예외를 발생한다.")
        @Test
        void testMapWithUnknownType() {
            // When & Then
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                ResultSetColumnReader.read(resultSet, "unknown", Double.class);
            });

            assertEquals("존재 하지 않는 타입의 필드 입니다.", thrown.getMessage());
        }
    }
}

