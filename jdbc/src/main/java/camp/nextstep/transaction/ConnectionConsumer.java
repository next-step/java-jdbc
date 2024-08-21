package camp.nextstep.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionConsumer {

    void accept(Connection c) throws SQLException;

}
