package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.function.TriConsumer;

public enum PreparedStatementSetter {
  INTEGER(Integer.class, (preparedStatement, index, value) -> {
    try {
      preparedStatement.setInt(index, (Integer) value);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  LONG(Long.class, (preparedStatement, index, value) -> {
    try {
      preparedStatement.setLong(index, (Long) value);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  STRING(String.class, (preparedStatement, index, value) -> {
    try {
      preparedStatement.setString(index, (String) value);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  BOOLEAN(Boolean.class, (preparedStatement, index, value) -> {
    try {
      preparedStatement.setBoolean(index, (Boolean) value);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  OBJECT(Object.class, (preparedStatement, index, value) -> {
    try {
      preparedStatement.setObject(index, value);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  ;

  private final Class<?> clazz;
  private final TriConsumer<PreparedStatement, Integer, Object> setter;

  private static final Map<Class<?>, PreparedStatementSetter> CLASS_TO_ENUM_MAP = new HashMap<>();

  static {
    for (PreparedStatementSetter setter : values()) {
      CLASS_TO_ENUM_MAP.put(setter.clazz, setter);
    }
  }

  PreparedStatementSetter(Class<?> clazz, TriConsumer<PreparedStatement, Integer, Object> setter) {
    this.clazz = clazz;
    this.setter = setter;
  }

  public static PreparedStatementSetter from(Class<?> clazz) {
    return CLASS_TO_ENUM_MAP.getOrDefault(clazz, OBJECT);
  }

  public void set(PreparedStatement preparedStatement, int index, Object value) {
    this.setter.accept(preparedStatement, index, value);
  }
}
