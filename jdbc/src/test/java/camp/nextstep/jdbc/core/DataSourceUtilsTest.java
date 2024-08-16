package camp.nextstep.jdbc.core;

import camp.nextstep.jdbc.datasource.DataSourceUtils;
import camp.nextstep.jdbc.support.DataSourceBean;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DataSourceUtilsTest {

    private DataSource dataSource = DataSourceBean.dataSource();

    @Test
    @DisplayName("새로운 Connection 을 반환 한다")
    void getConnection() throws Exception {
        // when
        final Connection actual = DataSourceUtils.getConnection(dataSource);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("Connection 을 리소스 반환 한다")
    @Test
    public void releaseConnection() throws Exception {
        // given
        final Connection connection = DataSourceUtils.getConnection(dataSource);

        // when
        DataSourceUtils.releaseConnection(dataSource);

        // then
        assertThat(connection.isClosed()).isTrue();
    }
}
