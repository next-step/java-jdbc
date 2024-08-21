package camp.nextstep.service;

import camp.nextstep.domain.User;

public interface UserService {
    User findByAccount(String account);

    User findById(long id);

    void save(User user);

    void changePassword(long id, String newPassword, String createBy);
}
