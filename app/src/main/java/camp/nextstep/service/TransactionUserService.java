package camp.nextstep.service;

import camp.nextstep.domain.User;
import camp.nextstep.jdbc.transaction.TransactionManager;

public class TransactionUserService implements UserService{

    private final UserService userService;
    private final TransactionManager transactionManager;

    public TransactionUserService(UserService userService, TransactionManager transactionManager) {
        this.userService = userService;
        this.transactionManager = transactionManager;
    }

    @Override
    public User findByAccount(String account) {
        return userService.findByAccount(account);
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
    public void changePassword(long id, String newPassword, String createBy) {
        transactionManager.getTransaction();
        try {
            userService.changePassword(id, newPassword, createBy);
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            throw e;
        }
    }
}
