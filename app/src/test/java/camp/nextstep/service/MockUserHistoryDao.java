package camp.nextstep.service;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.domain.UserHistory;
import camp.nextstep.jdbc.core.JdbcTemplate;

import java.sql.Connection;

public class MockUserHistoryDao extends UserHistoryDao {

    public MockUserHistoryDao(final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void log(final UserHistory userHistory) {
        throw new DataAccessException();
    }
}
