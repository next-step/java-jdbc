package camp.nextstep.jdbc.core;

import static org.mockito.Mockito.verify;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PreparedStatementParameterBinderTest {

  private PreparedStatement preparedStatement;

  @BeforeEach
  void setUp() {
    preparedStatement = Mockito.mock(PreparedStatement.class);
  }

  @Test
  @DisplayName("String 타입으로 바인딩한다")
  void testBindString() throws SQLException {
    PreparedStatementParameterBinder.bind(preparedStatement, 1, "test");

    verify(preparedStatement).setString(1, "test");
  }

  @Test
  @DisplayName("Long 타입으로 바인딩한다")
  void testBindLong() throws SQLException {
    PreparedStatementParameterBinder.bind(preparedStatement, 1, 100L);

    verify(preparedStatement).setLong(1, 100L);
  }

  @Test
  @DisplayName("Integer 타입으로 바인딩한다")
  void testBindInteger() throws SQLException {
    PreparedStatementParameterBinder.bind(preparedStatement, 1, 42);

    verify(preparedStatement).setInt(1, 42);
  }

  @Test
  @DisplayName("Boolean 타입으로 바인딩한다")
  void testBindBoolean() throws SQLException {
    PreparedStatementParameterBinder.bind(preparedStatement, 1, true);

    verify(preparedStatement).setBoolean(1, true);
  }

  @Test
  @DisplayName("오브젝트 타입으로 바인딩한다")
  void testBindObject() throws SQLException {
    Object obj = new Object();
    PreparedStatementParameterBinder.bind(preparedStatement, 1, obj);

    verify(preparedStatement).setObject(1, obj);
  }

  @Test
  @DisplayName("알 수 없는 타입일 경우 Object 타입으로 바인딩한다")
  void testBindUnknownTypeDefaultsToObject() throws SQLException {
    MyClass myClass = new MyClass();
    PreparedStatementParameterBinder.bind(preparedStatement, 1, myClass);

    verify(preparedStatement).setObject(1, myClass);
  }

  private static class MyClass {
  }

}