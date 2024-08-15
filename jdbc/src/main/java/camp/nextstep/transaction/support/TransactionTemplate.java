package camp.nextstep.transaction.support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class TransactionTemplate {

    private final DataSource dataSource;

    public TransactionTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void run(final Runnable runnable) {
        runInternal(() -> {
            runnable.run();
            return null;
        });
    }

    public <T> T run(final Supplier<T> supplier) {
        return runInternal(supplier);
    }

    private <T> T runInternal(final Supplier<T> supplier) {
        try (final Connection connection = getConnection()) {
            try {
                connection.setAutoCommit(false);

                final T result = supplier.get();

                connection.commit();
                return result;
            } catch (final Exception e) {
                connection.rollback();
                throw new TransactionException("transaction rollback for exception - %s".formatted(e.getClass().getSimpleName()), e);
            } finally {
                TransactionSynchronizationManager.unbindResource(dataSource);
            }
        } catch (final TransactionException e) {
            if (e.getCause() instanceof final RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw e;
        } catch (final Exception e) {
            TransactionSynchronizationManager.unbindResource(dataSource);
            throw new TransactionException("transaction fail to start", e);
        }
    }

    private Connection getConnection() throws SQLException {
        final Connection connection = TransactionSynchronizationManager.getResource(dataSource);

        if (connection != null) {
            return connection;
        }

        final Connection newConnection = dataSource.getConnection();
        TransactionSynchronizationManager.bindResource(dataSource, newConnection);

        return newConnection;
    }

}
