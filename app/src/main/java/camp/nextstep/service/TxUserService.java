package camp.nextstep.service;

import camp.nextstep.domain.User;
import camp.nextstep.transaction.Transaction;
import camp.nextstep.transaction.TransactionManager;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;

@Service
public class TxUserService implements UserService{

  private final UserService userService;
  private final TransactionManager transactionManager;

  @Autowired
  public TxUserService(UserService userService, TransactionManager transactionManager) {
    this.userService = userService;
    this.transactionManager = transactionManager;
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
    try (Transaction transaction = transactionManager.begin()) {
      userService.changePassword(id, newPassword, createBy);
      transaction.commit();
    }
  }
}
