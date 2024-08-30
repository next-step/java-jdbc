package camp.nextstep.service;

import camp.nextstep.domain.User;
import camp.nextstep.transaction.support.TransactionManager;
import camp.nextstep.transaction.support.TransactionTemplate;
import com.interface21.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

public class TxUserService implements UserService {
    private final UserService userService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public TxUserService(final UserService userService, DataSource dataSource) {
        this.userService = userService;

        TransactionManager transactionManager = new TransactionManager(dataSource);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
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
    public void changePassword(final long id, final String newPassword, final String createdBy) {
        transactionTemplate.execute(ignored -> {
            userService.changePassword(id, newPassword, createdBy);
            return null;
        });
    }
}
