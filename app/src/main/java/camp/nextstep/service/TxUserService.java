package camp.nextstep.service;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.datasource.DataSourceUtils;
import camp.nextstep.jdbc.datasource.TransactionalManager;
import camp.nextstep.transaction.support.TransactionStatus;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;
import javax.sql.DataSource;

@Service
public class TxUserService implements UserService {

    private final UserService userService;
    private final TransactionalManager transactionalManager;
    private final DataSource dataSource;

    @Autowired
    public TxUserService(UserService userService, TransactionalManager transactionalManager,
        DataSource dataSource) {
        this.userService = userService;
        this.transactionalManager = transactionalManager;
        this.dataSource = dataSource;
    }

    @Override
    public User findByAccount(String id) {
        return userService.findByAccount(id);
    }

    @Override
    public User findById(long id) {
        return userService.findById(id);
    }

    @Override
    public void save(User user) {
        userService.save(user);
    }

    @Override
    public void changePassword(long id, String newPassword, String createdBy) {
        TransactionStatus transactionStatus = transactionalManager.getTransaction(dataSource);

        try {
            userService.changePassword(id, newPassword, createdBy);
            transactionalManager.commit(transactionStatus, dataSource);
        } catch (Exception e) {
            transactionalManager.rollback(transactionStatus, dataSource);
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(dataSource);

        }
    }
}
