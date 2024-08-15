package camp.nextstep.support.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BeanPropertyRowMapper<T> {

    private final Class<T> mappedClass;

    public BeanPropertyRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

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

    public List<T> mapRows(ResultSet rs) throws SQLException {
        List<T> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapRow(rs));
        }
        return results;
    }
}
