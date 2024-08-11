package camp.nextstep.jdbc.sql;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqlTypeTest {

    @Test
    void 지원하지_않는_sqlTypeValue로_생성하려하는_경우_예외를_던진다() {
        assertThatThrownBy(() -> SqlType.from(Integer.MAX_VALUE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 sql 타입입니다.");
    }
}
