package camp.nextstep.dao;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("사용자를 찾을 수 없습니다. id=%s".formatted(id));
    }

    public UserNotFoundException(String account) {
        super("사용자를 찾을 수 없습니다. account=%s".formatted(account));
    }
}
