package camp.nextstep.jdbc.core;

import camp.nextstep.dao.QueryFormatException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JdbcTemplateTest {

    private final DataSource dataSource = mock(DataSource.class);
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    private final RowMapper<Member> rowMapper = rs -> new Member(rs.getLong("id"), rs.getString("name"));

    private final Connection connection = mock(Connection.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final ResultSet resultSet = mock(ResultSet.class);

    @Nested
    class query {

        @ParameterizedTest
        @ValueSource(strings = {"1", "1,jinyoung,nextstep"})
        void 쿼리의_placeholder만큼_매개변수가_없으면_예외가_발생한다(String givenArgs) {
            String sql = "select id, name from users where id = ? and name = ?";
            String[] args = givenArgs.split(",");

            assertThatThrownBy(() -> jdbcTemplate.query(sql, rowMapper, args))
                    .isInstanceOf(QueryFormatException.class)
                    .hasMessage("sql의 placeholder 수에 맞는 파라미터가 필요합니다.");
        }

        @Test
        void List에_데이터를_담아_반환한다() throws SQLException {
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement("select id, name from member")).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            when(resultSet.getLong("id")).thenReturn(1L).thenReturn(2L);
            when(resultSet.getString("name")).thenReturn("jin young").thenReturn("young jin");

            List<Member> actual = jdbcTemplate.query("select id, name from member", rowMapper);
            assertThat(actual).containsExactly(new Member(1L, "jin young"), new Member(2L, "young jin"));
        }

        @Test
        void 파라미터를_받아_List에_데이터를_담아_반환한다() throws SQLException {
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement("select id, name from member where name = ?")).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            when(resultSet.next()).thenReturn(true).thenReturn(false);
            when(resultSet.getLong("id")).thenReturn(1L);
            when(resultSet.getString("name")).thenReturn("jin young");

            List<Member> actual = jdbcTemplate.query("select id, name from member where name = ?", rowMapper, "jin young");
            assertThat(actual).containsExactly(new Member(1L, "jin young"));
        }
    }

    @Nested
    class queryForObject {

        @ParameterizedTest
        @ValueSource(strings = {"1", "1,jinyoung,nextstep"})
        void 쿼리의_placeholder만큼_매개변수가_없으면_예외가_발생한다(String givenArgs) {
            String sql = "select id, name from users where id = ? and name = ?";
            String[] args = givenArgs.split(",");

            assertThatThrownBy(() -> jdbcTemplate.queryForObject(sql, rowMapper, args))
                    .isInstanceOf(QueryFormatException.class)
                    .hasMessage("sql의 placeholder 수에 맞는 파라미터가 필요합니다.");
        }

        @Test
        void 쿼리를_실행한_ResultSet값을_매핑하여_반환한다() throws SQLException {
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement("select id, name from member where id = ?")).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            when(resultSet.next()).thenReturn(true);
            when(resultSet.getLong("id")).thenReturn(1L);
            when(resultSet.getString("name")).thenReturn("jin young");

            Optional<Member> actual = jdbcTemplate.queryForObject("select id, name from member where id = ?", rowMapper, 1);
            assertThat(actual).isEqualTo(Optional.of(new Member(1L, "jin young")));
        }

        @Test
        void 쿼리값이_존재하지_않는_경우_empty가_반환된다() throws SQLException {
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement("select id, name from member where id = ?")).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            Optional<Member> actual = jdbcTemplate.queryForObject(
                    "select id, name from member where id = ?",
                    rs -> new Member(rs.getLong("id"), rs.getString("name")),
                    1
            );
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class update {

        @ParameterizedTest
        @ValueSource(strings = {"1", "jinyoung,1,nextstep"})
        void 쿼리의_placeholder만큼_매개변수가_없으면_예외가_발생한다(String givenArgs) {
            String sql = "update member set name = ? where id = ?";
            String[] args = givenArgs.split(",");

            assertThatThrownBy(() -> jdbcTemplate.update(sql, args))
                    .isInstanceOf(QueryFormatException.class)
                    .hasMessage("sql의 placeholder 수에 맞는 파라미터가 필요합니다.");
        }

        @Test
        void 쿼리를_받아_preparedStatement를_실행한다() throws SQLException {
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement("update member set name = ? where id = ?")).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            int actual = jdbcTemplate.update("update member set name = ? where id = ?", "young jin", 1L);
            assertThat(actual).isEqualTo(1);
        }
    }

    private record Member(Long id, String name) {
    }
}
