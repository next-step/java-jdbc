package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.commons.lang3.ObjectUtils;

public enum ResultSetColumnReader {
  INTEGER(Integer.class, (resultSet, columnLabel) -> {
    try {
      return resultSet.getInt(columnLabel);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  LONG(Long.class, (resultSet, columnLabel) -> {
    try {
      return resultSet.getLong(columnLabel);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  STRING(String.class, (resultSet, columnLabel) -> {
    try {
      return resultSet.getString(columnLabel);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  BOOLEAN(Boolean.class, (resultSet, columnLabel) -> {
    try {
      return resultSet.getBoolean(columnLabel);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  OBJECT(Object.class, (resultSet, columnLabel) -> {
    try {
      return resultSet.getObject(columnLabel);
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }),
  ;

  private final Class<?> clazz;
  private final BiFunction<ResultSet, String, Object> reader;

  private static final Map<Class<?>, ResultSetColumnReader> CLASS_TO_ENUM_MAP = new HashMap<>();

  static {
    for (ResultSetColumnReader reader : values()) {
      CLASS_TO_ENUM_MAP.put(reader.clazz, reader);
    }
  }

  ResultSetColumnReader(Class<?> clazz, BiFunction<ResultSet, String, Object> reader) {
    this.clazz = clazz;
    this.reader = reader;
  }

  public static ResultSetColumnReader from(Class<?> clazz) {
    final ResultSetColumnReader reader = CLASS_TO_ENUM_MAP.get(clazz);
    if(ObjectUtils.isEmpty(reader)) {
      throw new IllegalArgumentException("This is a field of a type that does not exist.");
    }

    return reader;
  }

  public Object read(ResultSet resultSet, String columnLabel) {
    return reader.apply(resultSet, columnLabel);
  }
}
