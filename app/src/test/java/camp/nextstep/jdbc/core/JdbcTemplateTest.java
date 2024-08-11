package camp.nextstep.jdbc.core;

import camp.nextstep.config.MyConfiguration;
import camp.nextstep.domain.User;
import camp.nextstep.support.jdbc.init.DatabasePopulatorUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcTemplateTest {

    private static final DataSource dataSource = new MyConfiguration().dataSource();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    @BeforeAll
    static void setUp() {
        DatabasePopulatorUtils.execute(dataSource);
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    @DisplayName("insert query 를 받아 데이터를 추가 하면 데이터가 저장 된다")
    @Test
    public void update_insert() throws Exception {
        // given
        final User user = new User(2L, "account", "password", "email");
        final var sql = "insert into users (account, password, email) values (?, ?, ?)";

        // when
        jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail());

        // then
        final User actual = findUserById(user.getId());
        assertThat(actual).isNotNull()
                .extracting("id", "account", "password", "email")
                .contains(2L, "account", "password", "email");
    }

    public User findUserById(final Long id) {
        final String sql = "SELECT id, account, password, email FROM users WHERE id = ?";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (final ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getLong("id"),
                            resultSet.getString("account"),
                            resultSet.getString("password"),
                            resultSet.getString("email")
                    );
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 테이블 내 모든 데이터를 삭제하는 메서드
    public void cleanUp() {
        final String sql = "DELETE FROM users";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
