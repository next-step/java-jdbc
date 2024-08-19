package camp.nextstep.service;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.domain.User;
import camp.nextstep.transaction.TransactionHandler;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;
import javax.sql.DataSource;

@Service
public class TxUserService implements UserService {

    private final UserService userService;
    private final DataSource dataSource;
    private final TransactionHandler transactionHandler;

    @Autowired
    public TxUserService(final UserService userService, final DataSource dataSource, final TransactionHandler transactionHandler) {
        this.userService = userService;
        this.dataSource = dataSource;
        this.transactionHandler = transactionHandler;
    }

    @Override
    public User findByAccount(final String account) {
        return userService.findByAccount(account);
    }

    @Override
    public User findById(final long id) {
        return userService.findById(id);
    }

    @Override
    public void save(final User user) {
        userService.save(user);
    }

    @Override
    public void changePassword(final long id, final String newPassword, final String createBy) {
        try {
            transactionHandler.begin(dataSource);

            userService.changePassword(id, newPassword, createBy);

            transactionHandler.commit(dataSource);
        } catch (DataAccessException e) {
            transactionHandler.rollback(dataSource);
            throw e;
        }
    }
}
