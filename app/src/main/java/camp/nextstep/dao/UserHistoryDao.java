package camp.nextstep.dao;

import camp.nextstep.domain.UserHistory;
import camp.nextstep.jdbc.core.JdbcTemplate;
import camp.nextstep.jdbc.core.Sql;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Repository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class UserHistoryDao {

    private static final Logger log = LoggerFactory.getLogger(UserHistoryDao.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserHistoryDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void log(final UserHistory userHistory) {
        Sql sql = new Sql(
                "insert into user_history (user_id, account, password, email, created_at, created_by) values (?, ?, ?, ?, ?, ?)",
                List.of(userHistory.getUserId(), userHistory.getAccount(), userHistory.getPassword(), userHistory.getEmail(), userHistory.getCreatedAt(), userHistory.getCreatedBy()));
        jdbcTemplate.update(sql, pstmt -> {
            pstmt.setLong(1, userHistory.getUserId());
            pstmt.setString(2, userHistory.getAccount());
            pstmt.setString(3, userHistory.getPassword());
            pstmt.setString(4, userHistory.getEmail());
            pstmt.setString(5, userHistory.getEmail());
            pstmt.setString(6, userHistory.getCreatedAt().toString());
            pstmt.setString(7, userHistory.getCreatedBy());
        });
    }
}
