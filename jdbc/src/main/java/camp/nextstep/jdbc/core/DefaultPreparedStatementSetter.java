package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DefaultPreparedStatementSetter implements PreparedStatementSetter {
    private static final int FIRST_INDEX = 1;

    @Override
    public void setValues(PreparedStatement preparedStatement, Object... args){
        for (int i = FIRST_INDEX; i <= args.length; i++) {
            try {
                preparedStatement.setObject(i, args[i - 1]);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
