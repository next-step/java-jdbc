package camp.nextstep.jdbc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import camp.nextstep.domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GenericRowMapperTest {

    private ResultSet resultSet;

    @Test
    @DisplayName("ResultSet을 이용해서 RowMapper를 생성합니다.")
    void testGenericMapper() throws SQLException {
        resultSet = mock(ResultSet.class);

        when(resultSet.getObject("account")).thenReturn("gugu");
        when(resultSet.getObject("password")).thenReturn("password");
        when(resultSet.getObject("email")).thenReturn("hkkang@woowahan.com");

        final var user = new User("gugu", "password", "hkkang@woowahan.com");
        RowMapper<User> rowMapper = new GenericRowMapper<>(User.class);
        User actual = rowMapper.mapRow(resultSet);

        assertThat(actual).isEqualTo(user);
    }

}
