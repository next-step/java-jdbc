package camp.nextstep.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRowMapperTest {
    @DisplayName("싱글톤 객체인지 확인")
    @Test
    void isSingletonInstance() {
        UserRowMapper userRowMapper1 = UserRowMapper.getInstance();
        UserRowMapper userRowMapper2 = UserRowMapper.getInstance();

    	assertThat(userRowMapper1).isEqualTo(userRowMapper2);
    }

}