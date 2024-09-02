package camp.nextstep.transaction.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TransactionSynchronizationManagerTest {

  private DataSource dataSource;
  private Connection connection;

  @BeforeEach
  public void setUp() {
    dataSource = mock(DataSource.class);
    connection = mock(Connection.class);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // Reflection을 사용하여 private static final 필드에 접근하여 초기화
    Field resourcesField = TransactionSynchronizationManager.class.getDeclaredField("resources");
    resourcesField.setAccessible(true);
    ThreadLocal<Map<DataSource, Connection>> resources = (ThreadLocal<Map<DataSource, Connection>>) resourcesField.get(null);
    resources.set(new HashMap<>());
  }

  @Test
  @DisplayName("bindResource()를 호출하여 데이터 소스에 연결을 바인딩하고, getResource()를 통해 올바른 연결을 가져오는지 확인한다")
  public void testBindAndGetResource() {
    TransactionSynchronizationManager.bindResource(dataSource, connection);

    final Connection result = TransactionSynchronizationManager.getResource(dataSource);

    assertThat(result).isEqualTo(connection);
  }

  @Test
  @DisplayName("언바인딩된 연결이 처음 바인딩된 연결과 동일하다")
  public void testUnbindResource() {
    TransactionSynchronizationManager.bindResource(dataSource, connection);

    final Connection result = TransactionSynchronizationManager.unbindResource(dataSource);

    assertAll(
        () -> assertThat(result).isEqualTo(connection),
        () -> assertNull(TransactionSynchronizationManager.getResource(dataSource))
    );
  }

  @Test
  @DisplayName("자원이 바인딩되지 않은 상태에서 getResource()를 호출하면 null 이 반환된다")
  public void testGetResourceWhenNotBound() {
    final Connection result = TransactionSynchronizationManager.getResource(dataSource);

    assertNull(result);
  }

  @Test
  @DisplayName("자원이 바인딩되지 않은 상태에서 unbindResource()를 호출하면 null 이 반환된다")
  public void testUnbindResourceWhenNotBound() {
    final Connection result = TransactionSynchronizationManager.unbindResource(dataSource);

    assertNull(result);
  }
}