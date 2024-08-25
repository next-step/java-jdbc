package camp.nextstep.service;

import camp.nextstep.dao.UserDao;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.domain.User;
import camp.nextstep.domain.UserHistory;
import camp.nextstep.jdbc.datasource.TransactionalManager;
import camp.nextstep.transaction.support.TransactionStatus;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;

@Service
public class UserService {

    private final UserDao userDao;
    private final UserHistoryDao userHistoryDao;
    private final TransactionalManager transactionalManager;

    @Autowired
    public UserService(final UserDao userDao, final UserHistoryDao userHistoryDao, final
    TransactionalManager transactionalManager) {
        this.userDao = userDao;
        this.userHistoryDao = userHistoryDao;
        this.transactionalManager = transactionalManager;
    }

    public User findByAccount(final String account) {
        return userDao.findByAccount(account);
    }

    public User findById(final long id) {
        return userDao.findById(id);
    }

    public void save(final User user) {
        userDao.insert(user);
    }

    public void changePassword(final long id, final String newPassword, final String createBy) {

        TransactionStatus transactionStatus = transactionalManager.getTransaction();

        try {
            final var user = findById(id);
            user.changePassword(newPassword);
            userDao.update(user);
            userHistoryDao.log(new UserHistory(user, createBy));
            transactionalManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionalManager.rollback(transactionStatus);
            throw e;
        }

    }
}
