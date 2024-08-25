package camp.nextstep.jdbc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ResultSetColumnReaderTest {

  private ResultSet resultSet;

  @BeforeEach
  void setUp() {
    resultSet = Mockito.mock(ResultSet.class);
  }

  @Test
  @DisplayName("String 타입을 읽어온다")
  void testMapString() throws SQLException {
    when(resultSet.getString("name")).thenReturn("test");

    Object result = ResultSetColumnReader.get(resultSet, "name", String.class);

    assertAll(
        () -> assertThat(result).isEqualTo("test"),
        () -> verify(resultSet).getString("name")
    );
  }

  @Test
  @DisplayName("Long 타입을 읽어온다")
  void testMapLong() throws SQLException {
    when(resultSet.getLong("id")).thenReturn(100L);

    Object result = ResultSetColumnReader.get(resultSet, "id", Long.class);


    assertAll(
        () -> assertThat(result).isEqualTo(100L),
        () -> verify(resultSet).getLong("id")
    );
  }

  @Test
  @DisplayName("Integer 타입을 읽어온다")
  void testMapInteger() throws SQLException {
    when(resultSet.getInt("age")).thenReturn(42);

    Object result = ResultSetColumnReader.get(resultSet, "age", Integer.class);

    assertAll(
        () -> assertThat(result).isEqualTo(42),
        () -> verify(resultSet).getInt("age")
    );
  }

  @Test
  @DisplayName("Boolean 타입을 읽어온다")
  void testMapBoolean() throws SQLException {
    when(resultSet.getBoolean("is_active")).thenReturn(true);

    Object result = ResultSetColumnReader.get(resultSet, "is_active", Boolean.class);

    assertAll(
        () -> assertThat(result).isEqualTo(true),
        () -> verify(resultSet).getBoolean("is_active")
    );
  }

  @Test
  @DisplayName("Object 타입을 읽어온다")
  void testMapObject() throws SQLException {
    Object obj = new Object();
    when(resultSet.getObject("data")).thenReturn(obj);

    Object result = ResultSetColumnReader.get(resultSet, "data", Object.class);

    assertAll(
        () -> assertThat(result).isEqualTo(obj),
        () -> verify(resultSet).getObject("data")
    );
  }

  @Test
  @DisplayName("존재하지 않는 타입이라면 에러를 던진다")
  void testUnknownTypeThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      ResultSetColumnReader.get(resultSet, "unknown", MyClass.class);
    });
  }

  private static class MyClass {

  }

}