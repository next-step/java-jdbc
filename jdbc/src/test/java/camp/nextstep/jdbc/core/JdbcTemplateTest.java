package camp.nextstep.jdbc.core;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    private final ResultSet resultSet = mock(ResultSet.class);

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


    @Test
    @DisplayName("query 실행 시 객체들을 조회 후 리소스를 반환한다.")
    void testQuery() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(resultSet.getLong(1))
                .thenReturn(1L)
                .thenReturn(2L);
        when(resultSet.getString(2))
                .thenReturn("test")
                .thenReturn("test2");
        when(resultSet.getString(3))
                .thenReturn("password")
                .thenReturn("password");
        when(resultSet.getString(4))
                .thenReturn("test@test.com")
                .thenReturn("test2@test.com");

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        final String sql = "SELECT id, account, password, email FROM users";
        final List<User> users = jdbcTemplate.query(sql, rs -> new User(
                rs.getLong(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4)
        ));

        assertThat(users).containsExactly(
                new User(1L, "test", "password", "test@test.com"),
                new User(2L, "test2", "password", "test2@test.com")
        );

        verify(resultSet).close();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @Test
    @DisplayName("query 실행 시 SQLException 발생 시 DataAccessException 으로 예외를 던지고, 리소스를 회수한다.")
    void testQueryException() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        doThrow(new SQLException()).when(preparedStatement).executeQuery();

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertThatThrownBy(() -> jdbcTemplate.query(
                "SELECT id, account, password, email FROM users",
                rs -> new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4))
        )).isInstanceOf(DataAccessException.class);

        verify(preparedStatement).close();
        verify(connection).close();
    }

    private record User(Long id, String account, String password, String email) {

    }
}
