package camp.nextstep.dao;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    private static final UserRowMapper INSTANCE = new UserRowMapper();

    private UserRowMapper() {
    }

    public static UserRowMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public User mapRow(final ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4));
    }
}
