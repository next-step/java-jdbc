package camp.nextstep.transaction.support;

import java.sql.Connection;

public record TransactionStatus(Connection connection) {
    public void checkConnectionActive() {
        assert connection != null;
    }
}
