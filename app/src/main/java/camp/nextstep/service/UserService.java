package camp.nextstep.service;

import camp.nextstep.domain.User;

public interface UserService {

    User findByAccount(final String account);

    User findById(final long id);

    void save(final User user);

    void changePassword(final long id, final String newPassword, final String createBy);
}
