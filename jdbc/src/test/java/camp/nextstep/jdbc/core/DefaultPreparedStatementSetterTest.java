package camp.nextstep.jdbc.core;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DefaultPreparedStatementSetterTest {

    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final DefaultPreparedStatementSetter preparedStatementSetter = new DefaultPreparedStatementSetter(dataSource);

    @Test
    void 요청된_sql에_대한_쿼리를_실행한다() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("select id, name from member where id = ?")).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setObject(1, 1);

        Member actual = preparedStatementSetter.executeQuery(
                "select id, name from member where id = ?",
                preparedStatement1 -> new Member(1L, "jin young"),
                1
        );
        assertThat(actual).isEqualTo(new Member(1L, "jin young"));
        verify(preparedStatement).setObject(1, 1);
    }

    private record Member(Long id, String name) {
    }
}
