package camp.nextstep.jdbc.transaction;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlRunnable {

    void run() throws SQLException;
}
