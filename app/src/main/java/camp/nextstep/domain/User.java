package camp.nextstep.domain;

import java.util.Objects;

public class User {

    private final String account;
    private final String email;
    private Long id;
    private String password;

    public User() {
        this.account = "";
        this.email = "";
    }

    public User(long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        User user = (User) object;
        return Objects.equals(account, user.account) && Objects.equals(email,
            user.email) && Objects.equals(id, user.id) && Objects.equals(password,
            user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, email, id, password);
    }
}
