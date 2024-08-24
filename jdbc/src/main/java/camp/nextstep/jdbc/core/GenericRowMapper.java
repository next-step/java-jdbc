package camp.nextstep.jdbc.core;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenericRowMapper<T> implements RowMapper<T> {
    private Class<T> type;

    public GenericRowMapper(Class<T> type) {
        this.type = type;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            Field[] fields = type.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String columnName = field.getName();
                Object value = resultSet.getObject(columnName);
                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new SQLException("Failed to map row: " + type.getName(), e);
        }

    }
}
