package camp.nextstep.jdbc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import camp.nextstep.dao.DataAccessException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RowMapperImplTest {

  private ResultSet resultSet;
  private RowMapperImpl<TestEntity> rowMapper;

  @BeforeEach
  void setUp() {
    resultSet = Mockito.mock(ResultSet.class);
    rowMapper = new RowMapperImpl<>(TestEntity.class);
  }

  @Test
  @DisplayName("ResultSet 으로 부터 row 마다 값을 읽어 객체에 값을 매핑한다")
  void testMapRowSuccess() throws Exception {
    when(resultSet.getString("name")).thenReturn("heedoitdox");
    when(resultSet.getInt("age")).thenReturn(30);
    when(resultSet.getBoolean("active")).thenReturn(true);

    TestEntity entity = rowMapper.mapRow(resultSet);

    assertAll(
        () -> assertThat(entity).isNotNull(),
        () -> assertThat(entity.getName()).isEqualTo("heedoitdox"),
        () -> assertThat(entity.getAge()).isEqualTo(30)

    );
  }

  @Test
  @DisplayName("필드에 값을 세팅할 수 없는 경우 에러를 던진다")
  void testMapRowThrowsRuntimeException() throws Exception {
    doThrow(new SQLException("Column not found")).when(resultSet).getString("name");

    RuntimeException exception = assertThrows(DataAccessException.class, () -> {
      rowMapper.mapRow(resultSet);
    });

    assertAll(
        () -> assertThat(exception.getCause()).isInstanceOf(SQLException.class),
        () -> assertThat(exception.getCause().getMessage()).isEqualTo("Column not found")
    );
  }

  // Test entity class for mapping
  public static class TestEntity {
    private String name;
    private Integer age;

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }
  }

}