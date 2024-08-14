package camp.nextstep.service;

import camp.nextstep.dao.UserDao;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.domain.User;
import camp.nextstep.domain.UserHistory;
import camp.nextstep.transaction.TransactionCallback;
import camp.nextstep.transaction.TransactionHandler;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;
import javax.sql.DataSource;

@Service
public class UserService {

    private final DataSource dataSource;
    private final TransactionHandler transactionHandler;
    private final UserDao userDao;
    private final UserHistoryDao userHistoryDao;

    @Autowired
    public UserService(DataSource dataSource, TransactionHandler transactionHandler, final UserDao userDao, final UserHistoryDao userHistoryDao) {
        this.dataSource = dataSource;
        this.transactionHandler = transactionHandler;
        this.userDao = userDao;
        this.userHistoryDao = userHistoryDao;
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
        TransactionCallback txCallback = () -> targetMethod(id, newPassword, createBy);
        transactionHandler.executeWithTransaction(dataSource, txCallback);
    }

    private void targetMethod(long id, String newPassword, String createBy) {
        final var user = findById(id);
        user.changePassword(newPassword);
        userDao.update(user);
        userHistoryDao.log(new UserHistory(user, createBy));
    }
}
