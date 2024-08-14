package camp.nextstep.service;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.dao.UserDao;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.domain.User;
import camp.nextstep.domain.UserHistory;
import camp.nextstep.jdbc.datasource.ConnectionUtils;
import camp.nextstep.transaction.support.TransactionSynchronizationManager;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

@Service
public class UserService {

    private final DataSource dataSource;
    private final UserDao userDao;
    private final UserHistoryDao userHistoryDao;

    @Autowired
    public UserService(DataSource dataSource, final UserDao userDao, final UserHistoryDao userHistoryDao) {
        this.dataSource = dataSource;
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
        Connection connection = null;
        try {
            connection = ConnectionUtils.getConnection(dataSource);
            connection.setAutoCommit(false);

            final var user = findById(id);
            user.changePassword(newPassword);
            userDao.update(user);
            userHistoryDao.log(new UserHistory(user, createBy));

            connection.commit();
        } catch (SQLException | DataAccessException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                throw e;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            try {
                TransactionSynchronizationManager.unbindResource(dataSource);
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {}
        }
    }
}
