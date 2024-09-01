package camp.nextstep.jdbc.datasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import camp.nextstep.jdbc.CannotGetJdbcConnectionException;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DataSourceUtilsTest {

  private DataSource dataSource;
  private Connection connection;

  @BeforeEach
  public void setUp() {
    dataSource = mock(DataSource.class);
    connection = mock(Connection.class);
  }

  @Test
  @DisplayName("이미 바인딩된 트랜잭션이 있다면 그대로 반환한다")
  public void testGetConnectionWhenAlreadyBound() {
    TransactionSynchronizationManager.bindResource(dataSource, connection);

    final Connection result = DataSourceUtils.getConnection(dataSource);

    assertAll(
        () -> assertThat(result).isEqualTo(connection),
        () -> verify(dataSource, never()).getConnection()
    );
  }

  @Test
  @DisplayName("트랜잭션 동기화 관리자에 바인딩된 연결이 없을 때, 새로운 연결이 생성되고 바인딩된다")
  public void testGetConnectionWhenNotBound() throws SQLException {
    when(dataSource.getConnection()).thenReturn(connection);

    final Connection result = DataSourceUtils.getConnection(dataSource);


    assertAll(
        () -> assertThat(result).isEqualTo(connection),
        () -> verify(dataSource).getConnection(),
        () -> assertThat(result).isEqualTo(TransactionSynchronizationManager.getResource(dataSource))
    );
  }

  @Test
  @DisplayName("getConnection() 호출시 SQLException 이 발생하면 CannotGetJdbcConnectionException 가 발생한다")
  public void testGetConnectionThrowsCannotGetJdbcConnectionException() throws SQLException {
    when(dataSource.getConnection()).thenThrow(new SQLException());

    assertThrows(CannotGetJdbcConnectionException.class, () -> DataSourceUtils.getConnection(dataSource));
  }

  @Test
  @DisplayName("releaseConnection 메서드가 호출될 때, 트랜잭션 동기화 관리자에서 연결이 해제되는지 확인합니다.")
  public void testReleaseConnection() {
    TransactionSynchronizationManager.bindResource(dataSource, connection);
    DataSourceUtils.releaseConnection(dataSource);

    assertAll(
        () -> verify(connection).setAutoCommit(true),
        () -> verify(connection).close(),
        () -> assertNull(TransactionSynchronizationManager.getResource(dataSource))
    );
  }

  @Test
  @DisplayName("releaseConnection 호출 시 SQLException 이 발생하면 CannotGetJdbcConnectionException 이 발생한다.")
  public void testReleaseConnectionThrowsCannotGetJdbcConnectionException() throws SQLException {
    TransactionSynchronizationManager.bindResource(dataSource, connection);
    doThrow(new SQLException()).when(connection).close();

    assertThrows(CannotGetJdbcConnectionException.class, () -> DataSourceUtils.releaseConnection(dataSource));
  }
}