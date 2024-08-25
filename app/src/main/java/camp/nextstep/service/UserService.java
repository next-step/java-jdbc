package camp.nextstep.service;

import camp.nextstep.domain.User;
import com.interface21.context.stereotype.Service;

@Service
public interface UserService {

    User findByAccount(String account);

    User findById(final long id);

    void save(final User user);

    void changePassword(final long id, final String newPassword, final String createBy);

}
