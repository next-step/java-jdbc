package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class PreparedStatementParameterBinder {

  private interface Binder {
    void bind(PreparedStatement preparedStatement, int index, Object value) throws SQLException;
  }

  private static final Map<Class<?>, Binder> BINDER_MAP = new HashMap<>();

  static {
    BINDER_MAP.put(String.class, (preparedStatement, index, value) -> preparedStatement.setString(index, (String) value));
    BINDER_MAP.put(Long.class, (preparedStatement, index, value) -> preparedStatement.setLong(index, (Long) value));
    BINDER_MAP.put(Integer.class, (preparedStatement, index, value) -> preparedStatement.setInt(index, (Integer) value));
    BINDER_MAP.put(Boolean.class, (preparedStatement, index, value) -> preparedStatement.setBoolean(index, (Boolean) value));
    BINDER_MAP.put(Object.class, PreparedStatement::setObject);
  }

  public static void bind(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
    BINDER_MAP.getOrDefault(value.getClass(), BINDER_MAP.get(Object.class)).bind(preparedStatement, index, value);
  }
}