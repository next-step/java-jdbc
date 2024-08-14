package camp.nextstep.jdbc.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.util.Arrays;

public class RowMapper<T> {

    private final Class<T> clazz;

    public RowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T mapRow(ResultSet resultSet) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            setFieldValues(instance, resultSet);
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFieldValues(Object instance, ResultSet resultSet) {
        Arrays.stream(clazz.getDeclaredFields())
            .forEach(field -> {
                try {
                    field.setAccessible(true);
                    field.set(instance, ResultSetColumnReader.read(resultSet, field.getName(), field.getType()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
