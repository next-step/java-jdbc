package camp.nextstep.jdbc.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class RowMapperImpl<T> implements RowMapper<T> {

    private final Class<T> clazz;

    public RowMapperImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            setFieldValues(instance, clazz.getDeclaredFields(), resultSet);
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFieldValues(Object instance, Field[] fields, ResultSet resultSet) {
        Arrays.stream(fields)
            .forEach(field -> {
                try {
                    field.setAccessible(true);
                    field.set(instance, ResultSetColumnReader.map(resultSet, field.getName(), field.getType()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
