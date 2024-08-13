package camp.nextstep.jdbc.sql;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqlTypeTest {

    @Test
    void 지원하지_않는_sqlTypeValue로_생성하려하는_경우_예외를_던진다() {
        assertThatThrownBy(() -> SqlType.from(Integer.MAX_VALUE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 sql 타입입니다.");
    }

    @Test
    void sqlTypeValue를_받아_생성한다() {
        SqlType actual = SqlType.from(4);
        assertThat(actual).isEqualTo(SqlType.INTEGER);
    }

    @Test
    void SqlType에_불일치한_객체인_경우_false를_반환한다() {
        boolean actual = SqlType.BIG_INT.isSqlType("false");
        assertThat(actual).isFalse();
    }

    @Test
    void SqlType에_일치한_객체인_경우_true를_반환한다() {
        boolean actual = SqlType.BIG_INT.isSqlType(1L);
        assertThat(actual).isTrue();
    }
}
