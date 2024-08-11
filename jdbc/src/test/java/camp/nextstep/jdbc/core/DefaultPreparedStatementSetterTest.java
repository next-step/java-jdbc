package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DefaultPreparedStatementSetterTest {

    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final ParameterMetaData parameterMetaData = mock(ParameterMetaData.class);
    private final DefaultPreparedStatementSetter preparedStatementSetter = new DefaultPreparedStatementSetter(dataSource);

    @Test
    void 요청된_sql에_대한_쿼리를_실행한다() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("select id, name from member where id = ?")).thenReturn(preparedStatement);
        when(preparedStatement.getParameterMetaData()).thenReturn(parameterMetaData);
        when(parameterMetaData.getParameterCount()).thenReturn(1);
        doNothing().when(preparedStatement).setObject(1, 1);

        Member actual = preparedStatementSetter.executeQuery(
                "select id, name from member where id = ?",
                preparedStatement1 -> new Member(1L, "jin young"),
                1
        );
        assertThat(actual).isEqualTo(new Member(1L, "jin young"));
        verify(preparedStatement).setObject(1, 1);
    }

    @Test
    void 쿼리실행에서_오류가_발생하면_예외를_던진다() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("error id, name select")).thenThrow(new SQLException());

        assertThatThrownBy(() -> preparedStatementSetter.executeQuery("error id, name select", preparedStatement1 -> new Member(1L, "jin young")))
                .isInstanceOf(DataAccessException.class)
                .hasMessage("쿼리 실행 시 오류 발생");
    }

    @Test
    void PreparedStatement에_사용되는_파라미터_수와_요청된_파라미터수가_일치하지_않는_경우_예외를_던진다() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("select id, name from member id = ?")).thenReturn(preparedStatement);
        when(preparedStatement.getParameterMetaData()).thenReturn(parameterMetaData);
        when(parameterMetaData.getParameterCount()).thenReturn(1);

        assertThatThrownBy(() -> preparedStatementSetter.executeQuery("select id, name from member id = ?", preparedStatement1 -> new Member(1L, "jin young")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿼리 실행에 필요한 파리미터 수와 일치하지 않습니다.");
    }

    private record Member(Long id, String name) {
    }
}
