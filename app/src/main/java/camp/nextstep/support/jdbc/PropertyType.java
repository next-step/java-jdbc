package camp.nextstep.support.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Stream;

public enum PropertyType {
    STRING(String.class) {
        @Override
        public void setValue(Field field, Object instance, ResultSet rs) throws SQLException, IllegalAccessException {
            field.set(instance, rs.getString(field.getName()));
        }
    },
    LONG(long.class, Long.class) {
        @Override
        public void setValue(Field field, Object instance, ResultSet rs) throws SQLException, IllegalAccessException {
            field.set(instance, rs.getLong(field.getName()));
        }
    },
    INTEGER(int.class, Integer.class) {
        @Override
        public void setValue(Field field, Object instance, ResultSet rs) throws SQLException, IllegalAccessException {
            field.set(instance, rs.getInt(field.getName()));
        }
    };

    private final Class<?>[] types;

    PropertyType(Class<?>... types) {
        this.types = types;
    }

    public static PropertyType from(Class<?> clazz) {
        return Stream.of(values())
                     .filter(propertyType -> Arrays.asList(propertyType.types).contains(clazz))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 타입입니다."));
    }

    public abstract void setValue(Field field, Object instance, ResultSet rs) throws SQLException, IllegalAccessException;
}
