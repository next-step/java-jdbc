package camp.nextstep.jdbc.core;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementSetterImpl implements PreparedStatementSetter {
    private final Object[] args;

    public PreparedStatementSetterImpl(@Nullable Object[] args) {
        this.args = args;
    }

    @Override
    public void setValues(PreparedStatement psmt) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            psmt.setObject(i + 1, args[i]);
        }
    }
}
