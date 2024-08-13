package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArgumentPreparedStatementSetter implements PreparedStatementSetter {
    private final Object[] params;

    public ArgumentPreparedStatementSetter(Object[] params) {
        this.params = params;
    }

    @Override
    public void setValues(PreparedStatement pstmt) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }
}
