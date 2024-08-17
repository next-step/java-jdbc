package camp.nextstep.jdbc.core;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BeanPropertyRowMapper<T> implements RowMapper<T> {

    private final Class<T> mappedClass;

    public BeanPropertyRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    @Override
    public T mapRow(ResultSet rs) throws SQLException {
        try {
            T instance = mappedClass.getDeclaredConstructor().newInstance();
            Field[] fields = mappedClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                PropertyType propertyType = PropertyType.from(fieldType);

                if (propertyType != null) {
                    propertyType.setValue(field, instance, rs);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new SQLException("Error mapping row", e);
        }
    }
}
