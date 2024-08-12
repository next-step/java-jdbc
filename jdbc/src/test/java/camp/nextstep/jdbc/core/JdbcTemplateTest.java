package camp.nextstep.jdbc.core;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcTemplateTest {
    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);

    @Test
    @DisplayName("update 시 쿼리가 정상적으로 실행되고 리소스를 회수한다.")
    void testUpdate() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        final String sql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.update(sql, "test", "password", "test@test.com");

        verify(preparedStatement).setObject(1, "test");
        verify(preparedStatement).setObject(2, "password");
        verify(preparedStatement).setObject(3, "test@test.com");
        verify(preparedStatement).executeUpdate();
        verify(connection).close();
    }

    @Test
    @DisplayName("SQLException 발생 시 DataAccessException 으로 예외를 던지고, 리소스를 회수한다.")
    void testUpdateException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        doThrow(new SQLException()).when(preparedStatement).executeUpdate();

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertThatThrownBy(() -> jdbcTemplate.update("insert into users (account, password, email) values (?, ?, ?)", "test", "password", "test@test.com"))
                .isInstanceOf(DataAccessException.class);

        verify(preparedStatement).close();
        verify(connection).close();
    }
}
