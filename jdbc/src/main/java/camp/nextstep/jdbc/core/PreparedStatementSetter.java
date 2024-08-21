package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementSetter {
    void setValues(PreparedStatement psmt) throws SQLException;
}
