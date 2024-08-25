package camp.nextstep.jdbc.core;

import camp.nextstep.jdbc.DataMappingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperImpl<T> implements RowMapper<T> {

  private final Class<T> clazz;

  public RowMapperImpl(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public T mapRow(final ResultSet resultSet) {
    try {
      T instance = clazz.getDeclaredConstructor().newInstance();
      setFieldValues(instance, clazz.getDeclaredFields(), resultSet);
      return instance;
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private void setFieldValues(Object instance, Field[] fields, ResultSet resultSet) {
    for (Field field : fields) {
      try {
        setFieldValue(instance, field, resultSet);
      } catch (IllegalAccessException | SQLException e) {
        throw new DataMappingException("Failed to set field value for " + field.getName(), e);
      }
    }
  }

  private void setFieldValue(Object instance, Field field, ResultSet resultSet) throws IllegalAccessException, SQLException {
    field.setAccessible(true);
    final Object value = ResultSetColumnReader.get(resultSet, field.getName(), field.getType());
    field.set(instance, value);
  }
}