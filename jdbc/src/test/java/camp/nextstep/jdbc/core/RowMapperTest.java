package camp.nextstep.jdbc.core;

import java.sql.ResultSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("RowMapper 클래스의")
class RowMapperTest {

    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        resultSet = mock(ResultSet.class);
    }

    @DisplayName("mapRow 메서드는")
    @Nested
    class MapRow {
        @Test
        @DisplayName("ResultSet을 이용하여 객체를 생성한다.")
        void testMapRow() throws Exception {
            // Given
            when(resultSet.getString("name")).thenReturn("gugu");
            when(resultSet.getInt("age")).thenReturn(20);

            RowMapper<Person> rowMapper = new RowMapper<>(Person.class);

            // When
            Person person = rowMapper.mapRow(resultSet);

            // Then
            assertNotNull(person);
            assertEquals("gugu", person.getName());
            assertEquals(20, person.getAge());
        }

        @Test
        @DisplayName("기본 생성자가 없는 경우 예외를 발생한다.")
        void testMapRowWithNoDefaultConstructor() {
            RowMapper<NoDefaultConstructor> rowMapper = new RowMapper<>(NoDefaultConstructor.class);

            Exception exception = assertThrows(RuntimeException.class, () -> {
                rowMapper.mapRow(resultSet);
            });

            assertTrue(exception.getCause() instanceof NoSuchMethodException);
        }
    }

    static class Person {
        private String name;
        private int age;

        Person() {}

        String getName() {
            return name;
        }

        int getAge() {
            return age;
        }
    }

    static class NoDefaultConstructor {
        private String value;

        NoDefaultConstructor(String value) {
            this.value = value;
        }
    }
}
