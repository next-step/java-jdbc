package camp.nextstep.jdbc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JdbcTemplateTest {
    private static final String INSERT_QUERY = "insert into users (email) values (?)";
    private static final String SELECT_BY_ID_QUERY = "select id, email from users where id = ?";
    private static final String SELECT_BY_EMAIL_QUERY = "select id, email from users where email = ?";
    private static final String UPDATE_QUERY = "update users set email = ?";

    private static final ResultSetHandler<String> EMAIL_RESULT_SET_HANDLER = (rs) -> {
        try {
            return rs.getString("email");
        } catch (SQLException ignore) {
        }
        return "";
    };

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        final var jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
        jdbcDataSource.setUser("");
        jdbcDataSource.setPassword("");
        DatabaseUtils.execute(jdbcDataSource);

        jdbcTemplate = new JdbcTemplate(jdbcDataSource);
    }

    @DisplayName("데이터가 저장된다.")
    @Test
    void update() {
        String expected = "mail";
        jdbcTemplate.update(INSERT_QUERY, expected);

        long id = 1L;
        String actual = jdbcTemplate.selectOne(SELECT_BY_ID_QUERY, EMAIL_RESULT_SET_HANDLER, id).get();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("데이터가 수정된다.")
    @Test
    void update2() {
        jdbcTemplate.update(INSERT_QUERY, "mail");

        String expected = "updateMail";
        jdbcTemplate.update(UPDATE_QUERY, expected);

        long id = 1L;
        String actual = jdbcTemplate.selectOne(SELECT_BY_ID_QUERY, EMAIL_RESULT_SET_HANDLER, id).get();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("query의 매개변수 개수와 넘겨받은 인자의 개수가 서로 다를 경우 예외를 발생시킨다.")
    @Test
    void queryParamsCounts() {
        assertAll(
                () -> assertThatThrownBy(() -> jdbcTemplate.update(INSERT_QUERY))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> jdbcTemplate.update(INSERT_QUERY, "mail", "mail2"))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("데이터를 1개 가져온다.")
    @Test
    void selectOne() {
        jdbcTemplate.update(INSERT_QUERY, "mail");

        long id = 1L;
        String actual = jdbcTemplate.selectOne(SELECT_BY_ID_QUERY, EMAIL_RESULT_SET_HANDLER, id).get();

        assertThat(actual).isEqualTo("mail");
    }

    @DisplayName("데이터가 없으면 Optional.empty() 반환한다.")
    @Test
    void selectOne2() {
        jdbcTemplate.update(INSERT_QUERY, "mail");

        Optional<String> actual = jdbcTemplate.selectOne(SELECT_BY_ID_QUERY, EMAIL_RESULT_SET_HANDLER, 12342L);

        assertThat(actual).isEmpty();
    }

    @DisplayName("데이터가 2개 이상이면 예외를 발생시킨다.")
    @Test
    void selectOne3() {
        jdbcTemplate.update(INSERT_QUERY, "mail");
        jdbcTemplate.update(INSERT_QUERY, "mail");

        assertThatThrownBy(() -> jdbcTemplate.selectOne(SELECT_BY_EMAIL_QUERY, EMAIL_RESULT_SET_HANDLER, "mail"))
                .isInstanceOf(NotSingleResultSetException.class);
    }

    @DisplayName("해당하는 모든 결과를 찾는다.")
    @Test
    void selectAll() {
        jdbcTemplate.update(INSERT_QUERY, "mail");
        jdbcTemplate.update(INSERT_QUERY, "mail");
        jdbcTemplate.update(INSERT_QUERY, "mail");

        List<String> actual = jdbcTemplate.selectAll(SELECT_BY_EMAIL_QUERY, EMAIL_RESULT_SET_HANDLER, "mail");

        assertThat(actual).hasSize(3);
    }
}