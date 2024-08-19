package camp.nextstep.service;

import camp.nextstep.dao.UserDao;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.domain.User;
import camp.nextstep.domain.UserHistory;
import camp.nextstep.transaction.support.TransactionTemplate;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;

@Service
public class AppUserService implements UserService {

    private final UserDao userDao;
    private final UserHistoryDao userHistoryDao;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public AppUserService(final UserDao userDao, final UserHistoryDao userHistoryDao, final TransactionTemplate transactionTemplate) {
        this.userDao = userDao;
        this.userHistoryDao = userHistoryDao;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public User findByAccount(final String account) {
        return userDao.findByAccount(account);
    }

    @Override
    public User findById(final long id) {
        return userDao.findById(id);
    }

    @Override
    public void save(final User user) {
        userDao.insert(user);
    }

    @Override
    public void changePassword(final long id, final String newPassword, final String createBy) {
        transactionTemplate.run(() -> {
            final var user = findById(id);
            user.changePassword(newPassword);
            userDao.update(user);
            userHistoryDao.log(new UserHistory(user, createBy));
        });
    }

}
