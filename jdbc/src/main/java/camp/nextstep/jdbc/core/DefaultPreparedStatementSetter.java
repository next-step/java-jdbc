package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DefaultPreparedStatementSetter implements PreparedStatementSetter {

    private final Object[] parameters;

    public DefaultPreparedStatementSetter(final Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public void setValues(final PreparedStatement pstmt) throws SQLException {
        for (int i = 1; i <= parameters.length; i++) {
            pstmt.setObject(i, parameters[i - 1]);
        }
    }
}
