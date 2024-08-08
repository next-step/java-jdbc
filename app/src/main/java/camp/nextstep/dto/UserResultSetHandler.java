package camp.nextstep.dto;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.ResultSetHandler;
import com.interface21.context.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserResultSetHandler implements ResultSetHandler<User> {
    @Override
    public User handle(ResultSet rs) {
        try {
            return createUser(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User createUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("account"),
                rs.getString("password"),
                rs.getString("email")
        );
    }
}
