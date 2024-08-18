package camp.nextstep.support.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import camp.nextstep.jdbc.core.PropertyType;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertyTypeTest {

    private ResultSet resultSet;
    private TestClass testInstance;

    private static class TestClass {

        public String name;
        public long age;
        public int count;
    }

    @BeforeEach
    public void setUp() {
        resultSet = mock(ResultSet.class);
        testInstance = new TestClass();
    }

    @Test
    void setValue_String() throws Exception {
        Field field = TestClass.class.getField("name");
        when(resultSet.getString("name")).thenReturn("TestName");

        PropertyType.STRING.setValue(field, testInstance, resultSet);

        assertThat(testInstance.name).isEqualTo("TestName");
    }

    @Test
    void setValue_Long() throws Exception {
        Field field = TestClass.class.getField("age");
        when(resultSet.getLong("age")).thenReturn(30L);

        PropertyType.LONG.setValue(field, testInstance, resultSet);

        assertThat(testInstance.age).isEqualTo(30L);
    }

    @Test
    void setValue_Integer() throws Exception {
        Field field = TestClass.class.getField("count");
        when(resultSet.getInt("count")).thenReturn(100);

        PropertyType.INTEGER.setValue(field, testInstance, resultSet);

        assertThat(testInstance.count).isEqualTo(100);
    }

    @Test
    void testFrom() {
        assertThat(PropertyType.from(String.class)).isEqualTo(PropertyType.STRING);
        assertThat(PropertyType.from(Long.class)).isEqualTo(PropertyType.LONG);
        assertThat(PropertyType.from(Integer.class)).isEqualTo(PropertyType.INTEGER);
    }

    @Test
    void testFromUnsupportedType() {
        assertThatThrownBy(() -> PropertyType.from(Double.class))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
