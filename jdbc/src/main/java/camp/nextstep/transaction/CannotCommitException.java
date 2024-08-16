package camp.nextstep.transaction;

import java.sql.SQLException;

public class CannotCommitException extends RuntimeException {
    public CannotCommitException(String msg, SQLException e) {
        super(msg, e);
    }
}
