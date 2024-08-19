package camp.nextstep.transaction;

import camp.nextstep.transaction.support.ConnectionHolder;
import camp.nextstep.transaction.support.TransactionException;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceTransactionManager implements PlatformTransactionManager {

    private final DataSource dataSource;

    public DataSourceTransactionManager(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void getTransaction() {
        final ConnectionHolder connectionHolder = TransactionSynchronizationManager.getResource(dataSource);

        if (connectionHolder != null) {
            return;
        }
        try {
            final Connection newConnection = dataSource.getConnection();
            newConnection.setAutoCommit(false);
            TransactionSynchronizationManager.bindResource(dataSource, newConnection);
        } catch (final SQLException e) {
            throw new TransactionException("fail to open connection", e);
        }
    }

    @Override
    public void commit() {
        doCloseTransactionWith(Connection::commit);
    }

    @Override
    public void rollback() {
        doCloseTransactionWith(Connection::rollback);
    }

    private void doCloseTransactionWith(final ConnectionConsumer consumer) {
        final ConnectionHolder connectionHolder = TransactionSynchronizationManager.getResource(dataSource);

        if (connectionHolder == null) {
            throw new IllegalStateException("No connection found");
        }

        final Connection connection = connectionHolder.getConnection();

        try {
            consumer.accept(connection);
            connection.close();
        } catch (final SQLException e) {
            throw new TransactionException("fail to action with connection", e);
        }

        TransactionSynchronizationManager.unbindResource(dataSource);
    }
}
