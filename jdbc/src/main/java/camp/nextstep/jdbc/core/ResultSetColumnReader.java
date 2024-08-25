package camp.nextstep.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;

public class ResultSetColumnReader {

  private interface Reader {
    Object get(ResultSet rs, String columnName) throws SQLException;
  }

  private static final Map<Class<?>, Reader> MAP = new HashMap<>();

  static {
    MAP.put(String.class, ResultSet::getString);
    MAP.put(Long.class, ResultSet::getLong);
    MAP.put(Integer.class, ResultSet::getInt);
    MAP.put(Boolean.class, ResultSet::getBoolean);
    MAP.put(Object.class, ResultSet::getObject);
  }

  public static Object get(ResultSet rs, String columnName, Class<?> clazz) throws SQLException {
    Reader reader = MAP.get(clazz);
    if(ObjectUtils.isEmpty(reader)) {
      throw new IllegalArgumentException("This is a field of a type that does not exist.");
    }

    return reader.get(rs, columnName);
  }
}
