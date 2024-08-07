package camp.nextstep.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest {

    @Test
    void string에_sequence값이_몇개있는지_계산하여_반환한다() {
        long actual = StringUtils.countContainSequence("select id from users id = ?, name = ?", "?");
        assertThat(actual).isEqualTo(2);
    }
}
