package camp.nextstep.transaction.support;

import java.sql.Connection;

public class ConnectionHolder {
    private final Connection connection;
    private int referenceCount;

    public ConnectionHolder(final Connection connection) {
        this.connection = connection;
        this.referenceCount = 1;
    }

    public void requested() {
        ++this.referenceCount;
    }

    public void released() {
        --this.referenceCount;
    }

    public boolean isOpen() {
        return this.referenceCount > 0;
    }

    public Connection getConnection() {
        return connection;
    }
}
