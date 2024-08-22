package camp.nextstep.service;

import camp.nextstep.dao.UserDao;
import camp.nextstep.dao.UserHistoryDao;
import camp.nextstep.domain.User;
import camp.nextstep.domain.UserHistory;
import camp.nextstep.jdbc.datasource.TransactionManager;
import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Service;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public interface UserService {

    User findByAccount(final String account);

    User findById(final long id);

    void save(final User user);

    void changePassword(final long id, final String newPassword, final String createdBy);
}
