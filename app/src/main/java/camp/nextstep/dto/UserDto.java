package camp.nextstep.dto;

import camp.nextstep.domain.User;

public record UserDto(String account,
                      String password,
                      String email) {

    public User toEntity() {
        return new User(2L, account, password, email);
    }
}
