package camp.nextstep.jdbc.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DynamicResultSetHandler<T> implements ResultSetHandler<T> {

  private final Class<T> type;

  public DynamicResultSetHandler(Class<T> type) {
    this.type = type;
  }

  @Override
  public T handle(ResultSet rs) throws SQLException {
    try {
      Constructor<T> constructor = type.getDeclaredConstructor();
      constructor.setAccessible(true);
      T instance = constructor.newInstance();

      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();

      Map<String, Object> columnValues = new HashMap<>();
      for (int i = 1; i <= columnCount; i++) {
        String columnName = metaData.getColumnName(i).toLowerCase();
        Object value = rs.getObject(i);
        columnValues.put(columnName, value);
      }

      for (Field field : type.getDeclaredFields()) {
        field.setAccessible(true);
        String fieldName = field.getName().toLowerCase();
        if (columnValues.containsKey(fieldName)) {
          field.set(instance, columnValues.get(fieldName));
        }
      }

      return instance;
    } catch (ReflectiveOperationException e) {
      throw new SQLException("ResultSetHandler 생성 중 오류 발생: " + type.getName(), e);
    }
  }
}