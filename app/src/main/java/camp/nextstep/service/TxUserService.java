package camp.nextstep.service;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.core.JdbcException;
import camp.nextstep.jdbc.datasource.DataSourceUtils;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

@Service
public class TxUserService implements UserService {

    private static final Logger log = LoggerFactory.getLogger(TxUserService.class);

    private final AppUserService appUserService;
    private final DataSource dataSource;

    @Autowired
    public TxUserService(final AppUserService appUserService, final DataSource dataSource) {
        this.appUserService = appUserService;
        this.dataSource = dataSource;
    }

    @Override
    public User findByAccount(final String account) {
        return executeInTransaction(() -> appUserService.findByAccount(account));
    }

    @Override
    public User findById(final long id) {
        return executeInTransaction(() -> appUserService.findById(id));
    }

    @Override
    public void save(final User user) {
        executeInTransaction(() -> {
            appUserService.save(user);
            return null;
        });
    }

    @Override
    public void changePassword(final long id, final String newPassword, final String createdBy) {
        executeInTransaction(() -> {
            appUserService.changePassword(id, newPassword, createdBy);
            return null;
        });
    }

    private <T> T executeInTransaction(final Callable<T> action) {
        try {
            return startTx(action);
        } catch (RuntimeException e) {
            throw e;
        } catch (SQLException e) {
            throw new JdbcException("connection consume error - " + e.getMessage(), e);
        } catch (Exception e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    private <T> T startTx(final Callable<T> action) throws Exception {
        final Connection connection = DataSourceUtils.getConnection(dataSource);

        try {
            connection.setAutoCommit(false);
            T result = action.call();
            connection.commit();
            return result;
        } catch (RuntimeException e) {
            connection.rollback();
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(dataSource);
        }
    }
}
