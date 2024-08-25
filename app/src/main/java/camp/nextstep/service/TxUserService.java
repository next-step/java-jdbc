package camp.nextstep.service;

import camp.nextstep.domain.User;
import camp.nextstep.transaction.support.TransactionManager;
import camp.nextstep.transaction.support.TransactionTemplate;
import com.interface21.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

public class TxUserService implements UserService {

    private final UserService appUserService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public TxUserService(AppUserService appUserService, DataSource dataSource) {
        this.appUserService = appUserService;

        TransactionManager transactionManager = new TransactionManager(dataSource);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public User findByAccount(String account) {
        return appUserService.findByAccount(account);
    }

    @Override
    public User findById(long id) {
        return appUserService.findById(id);
    }

    @Override
    public void save(User user) {
        appUserService.save(user);
    }

    @Override
    public void changePassword(long id, String newPassword, String createBy) {
        transactionTemplate.execute(ignored -> {
            appUserService.changePassword(id, newPassword, createBy);
            return null;
        });
    }
}
