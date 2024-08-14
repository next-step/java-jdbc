package camp.nextstep.jdbc.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DefaultPreparedStatementSetterTest {

    @DisplayName("쿼리 문자열의 ? 순서에 맞게 파라미터를 매핑 한다")
    @Test
    public void setValues() throws Exception {
        // given
        final Object[] parameters = {"name", "password", 20};
        final DefaultPreparedStatementSetter setter = new DefaultPreparedStatementSetter(parameters);
        final PreparedStatement pstmt = mock(PreparedStatement.class);

        // when
        setter.setValues(pstmt);

        // then
        verify(pstmt).setObject(1, "name");
        verify(pstmt).setObject(2, "password");
        verify(pstmt).setObject(3, 20);
    }


}

