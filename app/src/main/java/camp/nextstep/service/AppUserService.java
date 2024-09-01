package camp.nextstep.service;

import camp.nextstep.dao.UserDao;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.domain.User;
import camp.nextstep.domain.UserHistory;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;

@Service
public class AppUserService implements UserService {

  private final UserDao userDao;
  private final UserHistoryDao userHistoryDao;

  @Autowired
  public AppUserService(UserDao userDao, UserHistoryDao userHistoryDao) {
    this.userDao = userDao;
    this.userHistoryDao = userHistoryDao;
  }

  @Override
  public User findByAccount(String account) {
    return userDao.findByAccount(account);
  }

  @Override
  public User findById(long id) {
    return userDao.findById(id);
  }

  @Override
  public void save(User user) {
    userDao.insert(user);
  }

  @Override
  public void changePassword(long id, String newPassword, String createdBy) {
    final var user = this.findById(id);
    user.changePassword(newPassword);
    userDao.update(user);
    userHistoryDao.log(new UserHistory(user, createdBy));
  }
}
