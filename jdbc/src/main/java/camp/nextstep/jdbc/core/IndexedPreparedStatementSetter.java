package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IndexedPreparedStatementSetter implements PreparedStatementSetter {
    private final Object[] values;

    public IndexedPreparedStatementSetter(final Object[] values) {
        this.values = values;
    }

    @Override
    public void setValues(final PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            preparedStatement.setObject(i + 1, values[i]);
        }
    }
}
