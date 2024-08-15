package camp.nextstep.transaction;

import java.sql.SQLException;

public class CannotRollbackException extends RuntimeException {
    public CannotRollbackException(String msg, SQLException e) {
        super(msg, e);
    }
}
