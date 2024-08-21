package camp.nextstep.service;


import camp.nextstep.domain.User;
import camp.nextstep.transaction.support.TransactionTemplate;
import com.interface21.context.stereotype.Service;

@Service
public class TxUserService implements UserService {

    private final UserService userService;
    private final TransactionTemplate transactionTemplate;

    public TxUserService(final UserService userService, final TransactionTemplate transactionTemplate) {
        this.userService = userService;
        this.transactionTemplate = transactionTemplate;
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
        transactionTemplate.run(() -> {
            userService.changePassword(id, newPassword, createBy);
        });
    }
}
