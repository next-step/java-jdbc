package camp.nextstep.transaction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TransactionTest {

  private DataSource dataSource;
  private Connection connection;
  private Transaction transaction;

  @BeforeEach
  public void setUp() throws SQLException {
    dataSource = mock(DataSource.class);
    connection = mock(Connection.class);

    when(dataSource.getConnection()).thenReturn(connection);
    transaction = new Transaction(dataSource);
  }

  @Test
  @DisplayName("commit 호출을 테스트한다")
  public void testCommit() throws SQLException {
    transaction.commit();

    verify(connection).commit();
    verify(connection, never()).rollback();
  }

  @Test
  @DisplayName("rollback 호출을 테스트한다")
  public void testRollback() throws SQLException {
    transaction.rollback();

    verify(connection).rollback();
    verify(connection, never()).commit();
  }

  @Test
  @DisplayName("commit 없이 리소스가 close 될 경우 rollback 을 호출한다")
  public void testCloseWithoutCommit() throws SQLException {
    transaction.close();

    verify(connection).rollback();
    verify(connection, never()).commit();
  }

  @Test
  @DisplayName("commit 을 한 뒤 리소스를 close 한다")
  public void testCloseWithCommit() throws SQLException {
    transaction.commit();
    transaction.close();

    verify(connection).commit();
    verify(connection, never()).rollback();
  }
}