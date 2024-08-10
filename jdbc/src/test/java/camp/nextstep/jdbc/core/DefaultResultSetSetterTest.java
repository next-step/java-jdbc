package camp.nextstep.jdbc.core;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultResultSetSetterTest {

    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final ResultSet resultSet = mock(ResultSet.class);
    private final RowMapper<Member> rowMapper = rs -> new Member(rs.getLong("id"), rs.getString("name"));

    private final DefaultResultSetSetter resultSetSetter = new DefaultResultSetSetter();

    @Test
    void preparedStatement쿼리를_실행하고_결과를_리스트로_반환한다() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getLong("id")).thenReturn(1L).thenReturn(2L);
        when(resultSet.getString("name")).thenReturn("jin young").thenReturn("young jin");

        List<Member> actual = resultSetSetter.parse(rowMapper, preparedStatement);
        assertThat(actual).containsExactly(new Member(1L, "jin young"), new Member(2L, "young jin"));
    }

    @Test
    void preparedStatement쿼리를_실행하고_결과를_Optional로_반환한다() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("jin young");

        Optional<Member> actual = resultSetSetter.parseToObject(rowMapper, preparedStatement);
        assertThat(actual).isEqualTo(Optional.of(new Member(1L, "jin young")));
    }

    private record Member(Long id, String name) {
    }
}
